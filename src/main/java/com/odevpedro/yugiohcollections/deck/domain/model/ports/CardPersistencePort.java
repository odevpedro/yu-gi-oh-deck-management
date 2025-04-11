package com.odevpedro.yugiohcollections.deck.domain.model.ports;

import com.odevpedro.yugiohcollections.deck.domain.model.Card;

public interface CardPersistencePort {
    Card save(Card card);
}