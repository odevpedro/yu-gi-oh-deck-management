package com.odevpedro.yugiohcollections.deck.domain.model.ports;

import com.odevpedro.yugiohcollections.deck.domain.model.Card;

import java.util.List;

public interface CardPersistencePort {
    Card save(Card card);

    List<Card> findAllByOwnerId(String ownerId);

    List<Card> updateCard();
}