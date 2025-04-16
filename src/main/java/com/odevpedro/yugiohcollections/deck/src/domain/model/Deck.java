package com.odevpedro.yugiohcollections.deck.src.domain.model;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Deck {

    private final Long id;
    private final String name;
    private final String ownerId;
    private final List<Long> mainDeck;
    private final List<Long> extraDeck;
    private final List<Long> sideDeck;

    public Deck(Long id, String name, String ownerId,
                List<Long> mainDeck, List<Long> extraDeck, List<Long> sideDeck) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.mainDeck = List.copyOf(mainDeck);
        this.extraDeck = List.copyOf(extraDeck);
        this.sideDeck = List.copyOf(sideDeck);

    }}