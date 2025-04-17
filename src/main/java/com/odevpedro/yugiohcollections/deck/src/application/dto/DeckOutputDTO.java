package com.odevpedro.yugiohcollections.deck.src.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DeckOutputDTO {

    private Long id;
    private String name;
    private String ownerId;
    private List<Long> mainDeck;
    private List<Long> extraDeck;
    private List<Long> sideDeck;

    public DeckOutputDTO(Long id, String name, String ownerId,
                         List<Long> mainDeck, List<Long> extraDeck, List<Long> sideDeck) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.mainDeck = mainDeck;
        this.extraDeck = extraDeck;
        this.sideDeck = sideDeck;
    }

}