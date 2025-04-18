package com.odevpedro.yugiohcollections.deck.application.mapper;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckEntity;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import org.springframework.stereotype.Component;

@Component
public class DeckMapper {

    public DeckEntity toEntity(Deck deck) {
        DeckEntity entity = new DeckEntity();
        entity.setId(deck.getId());
        entity.setName(deck.getName());
        entity.setOwnerId(deck.getOwnerId());
        entity.setMainDeck(deck.getMainDeck());
        entity.setExtraDeck(deck.getExtraDeck());
        entity.setSideDeck(deck.getSideDeck());
        return entity;
    }

    public Deck toDomain(DeckEntity entity) {
        return new Deck(
                entity.getId(),
                entity.getName(),
                entity.getOwnerId(),
                entity.getMainDeck(),
                entity.getExtraDeck(),
                entity.getSideDeck()
        );
    }
}