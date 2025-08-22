package com.odevpedro.yugiohcollections.deck.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter

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
        this.name = name;
        this.ownerId = ownerId;
        // garante listas não-nulas e tipadas
        this.mainDeck = (mainDeck != null) ? new ArrayList<>(mainDeck) : new ArrayList<>();
        this.extraDeck = (extraDeck != null) ? new ArrayList<>(extraDeck) : new ArrayList<>();
        this.sideDeck  = (sideDeck  != null) ? new ArrayList<>(sideDeck)  : new ArrayList<>();
    }

    public void addToMain(Long cardId)  { this.mainDeck.add(Objects.requireNonNull(cardId)); }
    public void addToExtra(Long cardId) { this.extraDeck.add(Objects.requireNonNull(cardId)); }
    public void addToSide(Long cardId)  { this.sideDeck.add(Objects.requireNonNull(cardId)); }

    public void removeFromMain(Long cardId)  { this.mainDeck.remove(cardId); }
    public void removeFromExtra(Long cardId) { this.extraDeck.remove(cardId); }
    public void removeFromSide(Long cardId)  { this.sideDeck.remove(cardId); }
}