package com.odevpedro.yugiohcollections.deck.application.dto;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DeckWithCardsDTO {
    private Long id;
    private String name;
    private String ownerId;
    private List<CardResponseDTO> mainDeck;
    private List<CardResponseDTO> extraDeck;
    private List<CardResponseDTO> sideDeck;
}