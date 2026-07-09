package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.service.DeckValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckView {

    private Long id;
    private String ownerId;
    private String name;
    private List<CardSummaryDTO> cards;
    private List<CardSummaryDTO> mainDeckCards;
    private List<CardSummaryDTO> extraDeckCards;
    private List<CardSummaryDTO> sideDeckCards;
    private int totalCards;
    private String notes;

    private int mainDeckSize;
    private int extraDeckSize;
    private int sideDeckSize;

    private boolean isValid;
    private List<String> validationErrors;

    public static DeckView from(Deck deck, Map<Long, CardSummaryDTO> cardInfoById) {
        if (cardInfoById == null) {
            cardInfoById = Map.of();
        }

        List<CardSummaryDTO> mainCards = expandZone(deck.getMainDeck(), cardInfoById);
        List<CardSummaryDTO> extraCards = expandZone(deck.getExtraDeck(), cardInfoById);
        List<CardSummaryDTO> sideCards = expandZone(deck.getSideDeck(), cardInfoById);
        List<CardSummaryDTO> normalized = new ArrayList<>();
        normalized.addAll(mainCards);
        normalized.addAll(extraCards);
        normalized.addAll(sideCards);

        int mainDeckSize = deck.getMainDeck() != null ? deck.getMainDeck().size() : 0;
        int extraDeckSize = deck.getExtraDeck() != null ? deck.getExtraDeck().size() : 0;
        int sideDeckSize = deck.getSideDeck() != null ? deck.getSideDeck().size() : 0;
        int total = mainDeckSize + extraDeckSize + sideDeckSize;

        return DeckView.builder()
                .id(deck.getId())
                .ownerId(deck.getOwnerId())
                .name(deck.getName())
                .cards(normalized)
                .mainDeckCards(mainCards)
                .extraDeckCards(extraCards)
                .sideDeckCards(sideCards)
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
                .mainDeckCards(List.of())
                .extraDeckCards(List.of())
                .sideDeckCards(List.of())
                .totalCards(total)
                .notes(null)
                .mainDeckSize(main)
                .extraDeckSize(extra)
                .sideDeckSize(side)
                .isValid(true)
                .validationErrors(List.of())
                .build();
    }

    private static List<CardSummaryDTO> expandZone(List<Long> ids, Map<Long, CardSummaryDTO> cardInfoById) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<CardSummaryDTO> zoneCards = new ArrayList<>();
        for (Long id : ids) {
            CardSummaryDTO info = cardInfoById.get(id);
            zoneCards.add(CardSummaryDTO.builder()
                    .cardId(id)
                    .name(info != null ? info.getName() : null)
                    .type(info != null ? info.getType() : null)
                    .imageUrl(info != null ? info.getImageUrl() : null)
                    .description(info != null ? info.getDescription() : null)
                    .atk(info != null ? info.getAtk() : null)
                    .def(info != null ? info.getDef() : null)
                    .level(info != null ? info.getLevel() : null)
                    .quantity(1)
                    .build());
        }
        return zoneCards;
    }
}
