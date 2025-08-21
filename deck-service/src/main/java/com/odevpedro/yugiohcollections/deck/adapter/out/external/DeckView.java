package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckCardEntryEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckEntity;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DeckView {

    private Long id;
    private String ownerId;
    private String name;
    private List<CardSummaryDTO> cards; // obtidos via card-service
    private int totalCards;
    private String notes;

    /**
     * Fábrica para quando você tem o domínio puro (Deck) com listas de cardIds,
     * e já recebeu do card-service a lista de cards enriquecidos (sem quantity).
     * totalCards = soma dos tamanhos (main + extra + side) OU cards.size() se preferir.
     */
    public static DeckView from(Deck deck, List<CardSummaryDTO> enrichedCards) {
        if (enrichedCards == null) enrichedCards = List.of();

        // Se o domínio não guarda quantidade por carta, default = 1
        List<CardSummaryDTO> normalized = enrichedCards.stream()
                .map(c -> c.getQuantity() == null
                        ? CardSummaryDTO.builder()
                        .cardId(c.getCardId())
                        .name(c.getName())
                        .type(c.getType())
                        .imageUrl(c.getImageUrl())
                        .description(c.getDescription())
                        .quantity(1)
                        .build()
                        : c)
                .collect(Collectors.toList());

        // total por zonas do domínio (se existirem)
        int main = deck.getMainDeck() != null ? deck.getMainDeck().size() : 0;
        int extra = deck.getExtraDeck() != null ? deck.getExtraDeck().size() : 0;
        int side = deck.getSideDeck() != null ? deck.getSideDeck().size() : 0;
        int total = (main + extra + side);

        // Se preferir confiar na lista “cards”:
        if (total == 0 && !normalized.isEmpty()) {
            total = normalized.stream().mapToInt(c -> Optional.ofNullable(c.getQuantity()).orElse(1)).sum();
        }

        return DeckView.builder()
                .id(deck.getId())
                .ownerId(deck.getOwnerId())
                .name(deck.getName())
                .cards(normalized)
                .totalCards(total)
                .notes(null) // ajuste se tiver campo de notas no seu domínio
                .build();
    }

    /**
     * Fábrica para quando você está no adapter de persistência e tem DeckEntity com entries (quantity).
     * Recebe um mapa de informações vindo do card-service (id -> CardSummaryDTO sem quantity),
     * e monta a lista final com quantity das entries.
     */
    public static DeckView from(DeckEntity deckEntity, Map<Long, CardSummaryDTO> cardInfoById) {
        if (cardInfoById == null) cardInfoById = Map.of();

        Map<Long, CardSummaryDTO> finalCardInfoById = cardInfoById;
        List<CardSummaryDTO> cards = deckEntity.getEntries().stream()
                .map(entry -> mergeEntryWithCardInfo(entry, finalCardInfoById.get(entry.getCardId())))
                .collect(Collectors.toList());

        int total = deckEntity.getEntries().stream()
                .mapToInt(DeckCardEntryEntity::getQuantity)
                .sum();

        return DeckView.builder()
                .id(deckEntity.getId())
                .ownerId(deckEntity.getOwnerId())
                .name(deckEntity.getName())
                .cards(cards)
                .totalCards(total)
                .notes(null) // ajuste se tiver notas no DeckEntity
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
}