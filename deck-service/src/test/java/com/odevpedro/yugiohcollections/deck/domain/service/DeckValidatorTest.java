package com.odevpedro.yugiohcollections.deck.domain.service;

import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DeckValidator — regras de composição do deck")
class DeckValidatorTest {

    private final DeckValidator validator = new DeckValidator();

    @Test
    void shouldAcceptACompleteAndBalancedDeck() {
        Deck deck = Deck.of("user-1", "Dragon Deck");
        for (long i = 1; i <= 40; i++) {
            deck.addToMain(i);
        }
        deck.addToExtra(100L);
        deck.addToSide(200L);

        DeckValidator.ValidationResult result = validator.validateDeck(deck);

        assertThat(result.isValid()).isTrue();
        assertThat(result.allErrors()).isEmpty();
    }

    @Test
    void shouldRejectCardCopyLimitViolations() {
        Deck deck = Deck.of("user-1", "Invalid Deck");
        deck.addToMain(1L);
        deck.addToMain(1L);
        deck.addToMain(1L);
        deck.addToMain(1L);

        DeckValidator.ValidationResult result = validator.validateDeck(deck);

        assertThat(result.isValid()).isFalse();
        assertThat(result.copyLimitErrors()).isNotEmpty();
    }
}
