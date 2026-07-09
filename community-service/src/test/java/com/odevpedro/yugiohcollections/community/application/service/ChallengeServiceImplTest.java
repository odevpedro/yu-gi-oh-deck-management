package com.odevpedro.yugiohcollections.community.application.service;

import com.odevpedro.yugiohcollections.community.adapter.out.external.DuelFeignClient;
import com.odevpedro.yugiohcollections.community.adapter.out.external.DuelResponseDTO;
import com.odevpedro.yugiohcollections.community.adapter.out.messaging.ChallengeEventPublisher;
import com.odevpedro.yugiohcollections.community.application.service.Impl.ChallengeServiceImpl;
import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import com.odevpedro.yugiohcollections.community.domain.model.ChallengeStatus;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.model.Player;
import com.odevpedro.yugiohcollections.community.domain.port.ChallengeRepositoryPort;
import com.odevpedro.yugiohcollections.community.domain.port.PlayerRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChallengeServiceImpl — orquestração de desafios")
class ChallengeServiceImplTest {

    @Mock
    private ChallengeRepositoryPort challengeRepository;

    @Mock
    private PlayerRepositoryPort playerRepository;

    @Mock
    private ChallengeEventPublisher eventPublisher;

    @Mock
    private DuelFeignClient duelFeignClient;

    private ChallengeServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ChallengeServiceImpl(challengeRepository, playerRepository, eventPublisher, duelFeignClient);
    }

    @Test
    void shouldCreateChallengeAndPublishEvent() {
        UUID challengerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        Challenge challenge = Challenge.of(challengerId, targetId, 10L, "duel me");

        when(playerRepository.findByUserId(targetId))
                .thenReturn(Optional.of(Player.of(targetId, "Kaiba", 0.0, 0.0, List.of("PC"))));
        when(challengeRepository.save(any(Challenge.class))).thenReturn(challenge);

        Challenge result = service.sendChallenge(challengerId, targetId, 10L, "duel me");

        assertThat(result.getTargetId()).isEqualTo(targetId);
        verify(eventPublisher).publishChallengeReceived(any(Challenge.class));
    }

    @Test
    void shouldAcceptChallengeAndCreateDuel() {
        UUID challengeId = UUID.randomUUID();
        UUID challengerId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        Challenge challenge = Challenge.builder()
                .id(challengeId)
                .challengerId(challengerId)
                .targetId(targetId)
                .challengerDeckId(11L)
                .message("duel")
                .status(ChallengeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        when(challengeRepository.findById(challengeId)).thenReturn(Optional.of(challenge), Optional.of(challenge));
        when(duelFeignClient.createDuel(any())).thenReturn(new DuelResponseDTO(
                "duel-1",
                challengerId.toString(),
                targetId.toString(),
                "DRAW",
                "ACTIVE",
                null,
                1,
                challengerId.toString()
        ));

        var result = service.accept(challengeId, targetId, 22L);

        assertThat(result.duelId()).isEqualTo("duel-1");
        verify(challengeRepository).updateStatus(challengeId, ChallengeStatus.ACCEPTED);
        verify(eventPublisher).publishChallengeAccepted(challenge);
    }
}
