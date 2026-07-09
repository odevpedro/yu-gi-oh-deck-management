package com.odevpedro.yugiohcollections.deck.adapter.out.persistence;

import com.odevpedro.yugiohcollections.deck.DeckServiceApplication;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.shared.security.TokenValidationClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(classes = DeckServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "jwt.skip-blacklist-check=true"
})
@DisplayName("DeckRepositoryAdapter — persistencia real com PostgreSQL")
class DeckRepositoryAdapterIT {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("deckdb")
            .withUsername("deck_user")
            .withPassword("deck_pass");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @MockBean
    private TokenValidationClient tokenValidationClient;

    @Autowired
    private DeckRepositoryAdapter deckRepositoryAdapter;

    @Test
    void shouldPersistAndReloadDeckWithAllZones() {
        Deck deck = Deck.of("user-1", "Dragon Deck");
        deck.addToMain(1001L);
        deck.addToMain(1002L);
        deck.addToExtra(2001L);
        deck.addToSide(3001L);

        Deck saved = deckRepositoryAdapter.save(deck);
        Optional<Deck> reloaded = deckRepositoryAdapter.findByIdAndOwnerId(saved.getId(), "user-1");

        assertThat(saved.getId()).isNotNull();
        assertThat(reloaded).isPresent();

        Deck result = reloaded.orElseThrow();
        assertThat(result.getMainDeck()).containsExactly(1001L, 1002L);
        assertThat(result.getExtraDeck()).containsExactly(2001L);
        assertThat(result.getSideDeck()).containsExactly(3001L);
        assertThat(deckRepositoryAdapter.findAllByOwnerId("user-1")).hasSize(1);
    }
}
