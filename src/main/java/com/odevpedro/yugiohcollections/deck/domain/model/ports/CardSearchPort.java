package com.odevpedro.yugiohcollections.deck.domain.model.ports;

import com.odevpedro.yugiohcollections.deck.domain.model.Card;

import java.util.List;

public interface CardSearchPort {
    List<Card> searchByName(String name);
}