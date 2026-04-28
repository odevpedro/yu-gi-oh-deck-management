package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckCardEntryEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckEntity;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.model.DeckZone;
import com.odevpedro.yugiohcollections.deck.domain.service.DeckValidator;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DeckView {

    private Long id;
    private String ownerId;
    private String name;
    private List<CardSummaryDTO> cards;
    private int totalCards;
    private String notes;

    private int mainDeckSize;
    private int extraDeckSize;
    private int sideDeckSize;

    private boolean isValid;
    private List<String> validationErrors;

    public static DeckView from(Deck deck, List<CardSummaryDTO> enrichedCards) {
        if (enrichedCards == null) enrichedCards = List.of();

        Map<Long, Long> idCountMap = new HashMap<>();
        for (Long id : deck.allCardIds()) {
            idCountMap.merge(id, 1L, Long::sum);
        }

        List<CardSummaryDTO> normalized = enrichedCards.stream()
                .map(c -> CardSummaryDTO.builder()
                        .cardId(c.getCardId())
                        .name(c.getName())
                        .type(c.getType())
                        .imageUrl(c.getImageUrl())
                        .description(c.getDescription())
                        .quantity(idCountMap.getOrDefault(c.getCardId(), 1L).intValue())
                        .build()
                )
                .toList();

        int mainDeckSize = deck.getMainDeck() != null ? deck.getMainDeck().size() : 0;
        int extraDeckSize = deck.getExtraDeck() != null ? deck.getExtraDeck().size() : 0;
        int sideDeckSize = deck.getSideDeck() != null ? deck.getSideDeck().size() : 0;
        int total = mainDeckSize + extraDeckSize + sideDeckSize;

        return DeckView.builder()
                .id(deck.getId())
                .ownerId(deck.getOwnerId())
                .name(deck.getName())
                .cards(normalized)
                .totalCards(total)
                .notes(null)
                .mainDeckSize(mainDeckSize)
                .extraDeckSize(extraDeckSize)
                .sideDeckSize(sideDeckSize)
                .isValid(true)
                .validationErrors(List.of())
                .build();
    }

    public void setValidation(DeckValidator.ValidationResult validation) {
        if (validation != null) {
            this.isValid = validation.isValid();
            this.validationErrors = validation.allErrors();
            this.mainDeckSize = validation.mainDeckSize();
            this.extraDeckSize = validation.extraDeckSize();
            this.sideDeckSize = validation.sideDeckSize();
        }
    }

    private static CardSummaryDTO mergeEntryWithCardInfo(DeckCardEntryEntity entry, CardSummaryDTO info) {
        return CardSummaryDTO.builder()
                .cardId(entry.getCardId())
                .quantity(entry.getQuantity())
                .name(info != null ? info.getName() : null)
                .type(info != null ? info.getType() : null)
                .imageUrl(info != null ? info.getImageUrl() : null)
                .description(info != null ? info.getDescription() : null)
                .build();
    }

    public static DeckView simple(Deck deck) {
        int main = deck.getMainDeck() != null ? deck.getMainDeck().size() : 0;
        int extra = deck.getExtraDeck() != null ? deck.getExtraDeck().size() : 0;
        int side = deck.getSideDeck() != null ? deck.getSideDeck().size() : 0;
        int total = main + extra + side;

        return DeckView.builder()
                .id(deck.getId())
                .ownerId(deck.getOwnerId())
                .name(deck.getName())
                .cards(List.of())
                .totalCards(total)
                .notes(null)
                .mainDeckSize(main)
                .extraDeckSize(extra)
                .sideDeckSize(side)
                .isValid(true)
                .validationErrors(List.of())
                .build();
    }
}

        List<CardSummaryDTO> normalized = enrichedCards.stream()
                .map(c -> CardSummaryDTO.builder()
                        .cardId(c.getCardId())
                        .name(c.getName())
                        .type(c.getType())
                        .imageUrl(c.getImageUrl())
                        .description(c.getDescription())
                        .quantity(idCountMap.getOrDefault(c.getCardId(), 1L).intValue())
                        .build()
                )
                .toList();

        int total = deck.allCardIds().size();

        return DeckView.builder()
                .id(deck.getId())
                .ownerId(deck.getOwnerId())
                .name(deck.getName())
                .cards(normalized)
                .totalCards(total)
                .notes(null)
                .build();
    }


    private static CardSummaryDTO mergeEntryWithCardInfo(DeckCardEntryEntity entry, CardSummaryDTO info) {
        return CardSummaryDTO.builder()
                .cardId(entry.getCardId())
                .quantity(entry.getQuantity())
                .name(info != null ? info.getName() : null)
                .type(info != null ? info.getType() : null)
                .imageUrl(info != null ? info.getImageUrl() : null)
                .description(info != null ? info.getDescription() : null)
                .build();
    }

    public static DeckView simple(Deck deck) {
        int main = deck.getMainDeck() != null ? deck.getMainDeck().size() : 0;
        int extra = deck.getExtraDeck() != null ? deck.getExtraDeck().size() : 0;
        int side = deck.getSideDeck() != null ? deck.getSideDeck().size() : 0;
        int total = main + extra + side;

        return DeckView.builder()
                .id(deck.getId())
                .ownerId(deck.getOwnerId())
                .name(deck.getName())
                .cards(List.of())
                .totalCards(total)
                .notes(null)
                .build();
    }
}