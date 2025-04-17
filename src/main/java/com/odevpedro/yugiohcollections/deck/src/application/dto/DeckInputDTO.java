package com.odevpedro.yugiohcollections.deck.src.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeckInputDTO {

    private String name;
    private String ownerId;
    private List<Long> mainDeck;
    private List<Long> extraDeck;
    private List<Long> sideDeck;

}
