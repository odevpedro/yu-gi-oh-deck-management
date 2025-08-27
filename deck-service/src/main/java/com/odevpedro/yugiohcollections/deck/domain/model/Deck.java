package com.odevpedro.yugiohcollections.deck.domain.model;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.DeckRepositoryAdapter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
public class Deck {

    private final Long id;
    private final String name;
    private final String ownerId;

    private final List<Long> mainDeck;
    private final List<Long> extraDeck;
    private final List<Long> sideDeck;

    public static Deck of(String ownerId, String name) {
        return Deck.builder()
                .ownerId(ownerId)
                .name(name)
                .mainDeck(new ArrayList<>())
                .extraDeck(new ArrayList<>())
                .sideDeck(new ArrayList<>())
                .build();
    }

    public void addToMain(Long cardId)  { this.mainDeck.add(Objects.requireNonNull(cardId)); }
    public void addToExtra(Long cardId) { this.extraDeck.add(Objects.requireNonNull(cardId)); }
    public void addToSide(Long cardId)  { this.sideDeck.add(Objects.requireNonNull(cardId)); }

    public void removeFromMain(Long cardId)  { this.mainDeck.remove(cardId); }
    public void removeFromExtra(Long cardId) { this.extraDeck.remove(cardId); }
    public void removeFromSide(Long cardId)  { this.sideDeck.remove(cardId); }

    public List<Long> allCardIds() {
        List<Long> all = new ArrayList<>();
        if (mainDeck != null) all.addAll(mainDeck);
        if (extraDeck != null) all.addAll(extraDeck);
        if (sideDeck != null) all.addAll(sideDeck);
        return all;
    }
}