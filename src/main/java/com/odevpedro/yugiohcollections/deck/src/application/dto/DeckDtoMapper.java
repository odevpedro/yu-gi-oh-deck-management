package com.odevpedro.yugiohcollections.deck.src.application.dto;

import com.odevpedro.yugiohcollections.deck.src.domain.model.Deck;
import org.springframework.stereotype.Component;

@Component
public class DeckDtoMapper {

    public Deck toDomain(DeckInputDTO dto) {
        return new Deck(
                null,
                dto.getName(),
                dto.getOwnerId(),
                dto.getMainDeck(),
                dto.getExtraDeck(),
                dto.getSideDeck()
        );
    }

    public DeckOutputDTO toOutput(Deck deck) {
        return new DeckOutputDTO(
                deck.getId(),
                deck.getName(),
                deck.getOwnerId(),
                deck.getMainDeck(),
                deck.getExtraDeck(),
                deck.getSideDeck()
        );
    }
}