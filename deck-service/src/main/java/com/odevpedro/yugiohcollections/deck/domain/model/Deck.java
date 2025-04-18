package com.odevpedro.yugiohcollections.deck.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Deck {

    private final Long id;
    private final String name;
    private final String ownerId;

    private final List<Long> mainDeck;
    private final List<Long> extraDeck;
    private final List<Long> sideDeck;
}
