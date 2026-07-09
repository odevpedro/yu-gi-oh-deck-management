package com.odevpedro.yugiohcollections.community.adapter.in.rest;

import com.odevpedro.yugiohcollections.community.CommunityServiceApplication;
import com.odevpedro.yugiohcollections.community.adapter.out.external.DuelFeignClient;
import com.odevpedro.yugiohcollections.community.adapter.out.external.DuelResponseDTO;
import com.odevpedro.yugiohcollections.community.adapter.out.messaging.ChallengeEventPublisher;
import com.odevpedro.yugiohcollections.community.application.service.ChallengeService;
import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.model.Player;
import com.odevpedro.yugiohcollections.community.domain.port.ChallengeRepositoryPort;
import com.odevpedro.yugiohcollections.community.domain.port.PlayerRepositoryPort;
import com.odevpedro.yugiohcollections.shared.security.TokenValidationClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = CommunityServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "jwt.skip-blacklist-check=true"
})
@DisplayName("Community Kafka flow — desafio aceito e duelo encerrado")
class DuelLifecycleKafkaFlowIT {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgis/postgis:16-3.4")
            .withDatabaseName("communitydb")
            .withUsername("community_user")
            .withPassword("community_pass")
            .withInitScript("test-init.sql");

    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.1")
    );

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("duel-service.url", () -> "http://localhost:18084");
    }

    @MockBean
    private TokenValidationClient tokenValidationClient;

    @MockBean
    private DuelFeignClient duelFeignClient;

    @MockBean
    private ChallengeEventPublisher challengeEventPublisher;

    @Autowired
    private ChallengeRepositoryPort challengeRepository;

    @Autowired
    private PlayerRepositoryPort playerRepository;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void shouldMovePlayersThroughChallengeAndDuelLifecycle() throws Exception {
        UUID challengerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        playerRepository.save(Player.of(challengerId, "Yugi", -23.5505, -46.6333, java.util.List.of("PC")));
        playerRepository.save(Player.of(targetId, "Kaiba", -23.5500, -46.6340, java.util.List.of("PC")));

        Challenge challenge = challengeRepository.save(Challenge.of(challengerId, targetId, 101L, "duel agora"));
        when(duelFeignClient.createDuel(any())).thenReturn(new DuelResponseDTO(
                "duel-123",
                challengerId.toString(),
                targetId.toString(),
                "DRAW",
                "ACTIVE",
                null,
                1,
                challengerId.toString()
        ));

        var accepted = challengeService.accept(challenge.getId(), targetId, 202L);

        assertThat(accepted.duelId()).isEqualTo("duel-123");
        assertThat(playerRepository.findByUserId(challengerId).orElseThrow().getDuelStatus())
                .isEqualTo(DuelStatus.IN_DUEL);
        assertThat(playerRepository.findByUserId(targetId).orElseThrow().getDuelStatus())
                .isEqualTo(DuelStatus.IN_DUEL);

        kafkaTemplate.send("duel.encerrado", Map.of(
                "playerAId", challengerId.toString(),
                "playerBId", targetId.toString()
        )).get(10, TimeUnit.SECONDS);
        kafkaTemplate.flush();

        waitForStatus(challengerId, DuelStatus.AVAILABLE);
        waitForStatus(targetId, DuelStatus.AVAILABLE);
    }

    private void waitForStatus(UUID userId, DuelStatus expected) throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            DuelStatus current = playerRepository.findByUserId(userId)
                    .map(Player::getDuelStatus)
                    .orElseThrow();
            if (current == expected) {
                return;
            }
            Thread.sleep(250L);
        }

        fail("Status do jogador " + userId + " nao mudou para " + expected);
    }
}
