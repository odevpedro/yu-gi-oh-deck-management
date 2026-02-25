package com.odevpedro.yugiohcollections.deck.domain;

import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Deck — regras de negócio do domínio")
class DeckTest {

    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = Deck.of("user-1", "Blue-Eyes White Dragon Deck");
    }

    @Test
    @DisplayName("deve criar deck vazio com as três zonas inicializadas")
    void shouldCreateDeckWithEmptyZones() {
        assertThat(deck.getMainDeck()).isEmpty();
        assertThat(deck.getExtraDeck()).isEmpty();
        assertThat(deck.getSideDeck()).isEmpty();
        assertThat(deck.getOwnerId()).isEqualTo("user-1");
        assertThat(deck.getName()).isEqualTo("Blue-Eyes White Dragon Deck");
    }

    @Test
    @DisplayName("deve adicionar carta ao main deck")
    void shouldAddCardToMainDeck() {
        deck.addToMain(1001L);

        assertThat(deck.getMainDeck()).containsExactly(1001L);
    }

    @Test
    @DisplayName("deve adicionar carta ao extra deck")
    void shouldAddCardToExtraDeck() {
        deck.addToExtra(2001L);

        assertThat(deck.getExtraDeck()).containsExactly(2001L);
    }

    @Test
    @DisplayName("deve adicionar carta ao side deck")
    void shouldAddCardToSideDeck() {
        deck.addToSide(3001L);

        assertThat(deck.getSideDeck()).containsExactly(3001L);
    }

    @Test
    @DisplayName("deve lançar NullPointerException ao adicionar cardId nulo")
    void shouldThrowWhenAddingNullCardId() {
        assertThatThrownBy(() -> deck.addToMain(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("deve remover carta do main deck")
    void shouldRemoveCardFromMainDeck() {
        deck.addToMain(1001L);
        deck.addToMain(1002L);

        deck.removeFromMain(1001L);

        assertThat(deck.getMainDeck()).containsExactly(1002L);
    }

    @Test
    @DisplayName("deve retornar todas as cartas das três zonas em allCardIds")
    void shouldReturnAllCardIdsFromAllZones() {
        deck.addToMain(1001L);
        deck.addToMain(1002L);
        deck.addToExtra(2001L);
        deck.addToSide(3001L);

        assertThat(deck.allCardIds())
                .hasSize(4)
                .containsExactlyInAnyOrder(1001L, 1002L, 2001L, 3001L);
    }

    @Test
    @DisplayName("deve permitir adicionar a mesma carta múltiplas vezes (copies)")
    void shouldAllowMultipleCopiesOfSameCard() {
        deck.addToMain(1001L);
        deck.addToMain(1001L);
        deck.addToMain(1001L);

        assertThat(deck.getMainDeck())
                .hasSize(3)
                .containsOnly(1001L);
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não há cartas em nenhuma zona")
    void shouldReturnEmptyListWhenNoCards() {
        assertThat(deck.allCardIds()).isEmpty();
    }
}