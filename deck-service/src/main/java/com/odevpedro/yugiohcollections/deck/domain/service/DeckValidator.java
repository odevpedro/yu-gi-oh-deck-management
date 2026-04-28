package com.odevpedro.yugiohcollections.deck.domain.service;

import com.odevpedro.yugiohcollections.deck.domain.exception.CardCopyLimitException;
import com.odevpedro.yugiohcollections.deck.domain.exception.DeckSizeLimitException;
import com.odevpedro.yugiohcollections.deck.domain.exception.DeckValidationException;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.model.DeckZone;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeckValidator {

    public static final int MAIN_DECK_MIN = 40;
    public static final int MAIN_DECK_MAX = 60;
    public static final int EXTRA_DECK_MAX = 15;
    public static final int SIDE_DECK_MAX = 15;
    public static final int MAX_COPIES_PER_CARD = 3;

    public void validateAddCard(Deck deck, Long cardId, int quantity, DeckZone zone) {
        List<String> violations = new ArrayList<>();

        int currentCopies = countCardCopies(deck, cardId);
        int newCopies = currentCopies + quantity;

        if (newCopies > MAX_COPIES_PER_CARD) {
            violations.add(String.format(
                "Carta ID %d: nao e permitido ter mais de %d copias no deck (atual: %d, tentando adicionar: %d)",
                cardId, MAX_COPIES_PER_CARD, currentCopies, quantity
            ));
        }

        int deckSizeAfter = getZoneSize(deck, zone) + quantity;
        if (zone == DeckZone.MAIN) {
            if (deckSizeAfter > MAIN_DECK_MAX) {
                violations.add(String.format(
                    "Main Deck nao pode ter mais de %d cartas (atual: %d, apos adicionar: %d)",
                    MAIN_DECK_MAX, getZoneSize(deck, zone), deckSizeAfter
                ));
            }
        } else if (zone == DeckZone.EXTRA) {
            if (deckSizeAfter > EXTRA_DECK_MAX) {
                violations.add(String.format(
                    "Extra Deck nao pode ter mais de %d cartas (atual: %d, apos adicionar: %d)",
                    EXTRA_DECK_MAX, getZoneSize(deck, zone), deckSizeAfter
                ));
            }
        } else if (zone == DeckZone.SIDE) {
            if (deckSizeAfter > SIDE_DECK_MAX) {
                violations.add(String.format(
                    "Side Deck nao pode ter mais de %d cartas (atual: %d, apos adicionar: %d)",
                    SIDE_DECK_MAX, getZoneSize(deck, zone), deckSizeAfter
                ));
            }
        }

        if (!violations.isEmpty()) {
            throw new DeckValidationException(violations);
        }
    }

    public void validateRemoveCard(Deck deck, Long cardId, DeckZone zone) {
        int currentCopies = getZoneCardCopies(deck, zone, cardId);
        if (currentCopies <= 0) {
            throw new IllegalArgumentException(String.format(
                "Carta ID %d nao existe na zona %s do deck", cardId, zone
            ));
        }
    }

    public void validateDeckComplete(Deck deck) {
        List<String> violations = new ArrayList<>();

        int mainSize = getZoneSize(deck, DeckZone.MAIN);
        int extraSize = getZoneSize(deck, DeckZone.EXTRA);
        int sideSize = getZoneSize(deck, DeckZone.SIDE);

        if (mainSize < MAIN_DECK_MIN) {
            violations.add(String.format(
                "Main Deck precisa de no minimo %d cartas (atual: %d)",
                MAIN_DECK_MIN, mainSize
            ));
        }

        if (mainSize > MAIN_DECK_MAX) {
            violations.add(String.format(
                "Main Deck nao pode ter mais de %d cartas (atual: %d)",
                MAIN_DECK_MAX, mainSize
            ));
        }

        if (extraSize > EXTRA_DECK_MAX) {
            violations.add(String.format(
                "Extra Deck nao pode ter mais de %d cartas (atual: %d)",
                EXTRA_DECK_MAX, extraSize
            ));
        }

        if (sideSize > SIDE_DECK_MAX) {
            violations.add(String.format(
                "Side Deck nao pode ter mais de %d cartas (atual: %d)",
                SIDE_DECK_MAX, sideSize
            ));
        }

        Map<Long, Integer> cardCounts = getAllCardCounts(deck);
        for (Map.Entry<Long, Integer> entry : cardCounts.entrySet()) {
            if (entry.getValue() > MAX_COPIES_PER_CARD) {
                violations.add(String.format(
                    "Carta ID %d: limite de %d copias excedido (quantidade: %d)",
                    entry.getKey(), MAX_COPIES_PER_CARD, entry.getValue()
                ));
            }
        }

        if (!violations.isEmpty()) {
            throw new DeckValidationException(violations);
        }
    }

    public ValidationResult validateDeck(Deck deck) {
        List<String> mainDeckErrors = new ArrayList<>();
        List<String> extraDeckErrors = new ArrayList<>();
        List<String> sideDeckErrors = new ArrayList<>();
        List<String> copyLimitErrors = new ArrayList<>();

        int mainSize = getZoneSize(deck, DeckZone.MAIN);
        int extraSize = getZoneSize(deck, DeckZone.EXTRA);
        int sideSize = getZoneSize(deck, DeckZone.SIDE);

        if (mainSize < MAIN_DECK_MIN) {
            mainDeckErrors.add(String.format("Minimo de %d cartas", MAIN_DECK_MIN));
        }
        if (mainSize > MAIN_DECK_MAX) {
            mainDeckErrors.add(String.format("Maximo de %d cartas", MAIN_DECK_MAX));
        }
        if (extraSize > EXTRA_DECK_MAX) {
            extraDeckErrors.add(String.format("Maximo de %d cartas", EXTRA_DECK_MAX));
        }
        if (sideSize > SIDE_DECK_MAX) {
            sideDeckErrors.add(String.format("Maximo de %d cartas", SIDE_DECK_MAX));
        }

        Map<Long, Integer> cardCounts = getAllCardCounts(deck);
        for (Map.Entry<Long, Integer> entry : cardCounts.entrySet()) {
            if (entry.getValue() > MAX_COPIES_PER_CARD) {
                copyLimitErrors.add(String.format(
                    "Carta %d: %d/%d copias",
                    entry.getKey(), entry.getValue(), MAX_COPIES_PER_CARD
                ));
            }
        }

        boolean isValid = mainDeckErrors.isEmpty() &&
                          extraDeckErrors.isEmpty() &&
                          sideDeckErrors.isEmpty() &&
                          copyLimitErrors.isEmpty();

        return new ValidationResult(
            isValid,
            mainDeckErrors,
            extraDeckErrors,
            sideDeckErrors,
            copyLimitErrors,
            mainSize,
            extraSize,
            sideSize
        );
    }

    public int countCardCopies(Deck deck, Long cardId) {
        int count = 0;
        if (deck.getMainDeck() != null) {
            count += Collections.frequency(deck.getMainDeck(), cardId);
        }
        if (deck.getExtraDeck() != null) {
            count += Collections.frequency(deck.getExtraDeck(), cardId);
        }
        if (deck.getSideDeck() != null) {
            count += Collections.frequency(deck.getSideDeck(), cardId);
        }
        return count;
    }

    public int getZoneSize(Deck deck, DeckZone zone) {
        return switch (zone) {
            case MAIN -> deck.getMainDeck() != null ? deck.getMainDeck().size() : 0;
            case EXTRA -> deck.getExtraDeck() != null ? deck.getExtraDeck().size() : 0;
            case SIDE -> deck.getSideDeck() != null ? deck.getSideDeck().size() : 0;
        };
    }

    private int getZoneCardCopies(Deck deck, DeckZone zone, Long cardId) {
        return switch (zone) {
            case MAIN -> deck.getMainDeck() != null ? Collections.frequency(deck.getMainDeck(), cardId) : 0;
            case EXTRA -> deck.getExtraDeck() != null ? Collections.frequency(deck.getExtraDeck(), cardId) : 0;
            case SIDE -> deck.getSideDeck() != null ? Collections.frequency(deck.getSideDeck(), cardId) : 0;
        };
    }

    private Map<Long, Integer> getAllCardCounts(Deck deck) {
        Map<Long, Integer> counts = new HashMap<>();

        if (deck.getMainDeck() != null) {
            for (Long cardId : deck.getMainDeck()) {
                counts.merge(cardId, 1, Integer::sum);
            }
        }
        if (deck.getExtraDeck() != null) {
            for (Long cardId : deck.getExtraDeck()) {
                counts.merge(cardId, 1, Integer::sum);
            }
        }
        if (deck.getSideDeck() != null) {
            for (Long cardId : deck.getSideDeck()) {
                counts.merge(cardId, 1, Integer::sum);
            }
        }

        return counts;
    }

    public record ValidationResult(
        boolean isValid,
        List<String> mainDeckErrors,
        List<String> extraDeckErrors,
        List<String> sideDeckErrors,
        List<String> copyLimitErrors,
        int mainDeckSize,
        int extraDeckSize,
        int sideDeckSize
    ) {
        public List<String> allErrors() {
            List<String> all = new ArrayList<>();
            all.addAll(mainDeckErrors);
            all.addAll(extraDeckErrors);
            all.addAll(sideDeckErrors);
            all.addAll(copyLimitErrors);
            return all;
        }
    }
}