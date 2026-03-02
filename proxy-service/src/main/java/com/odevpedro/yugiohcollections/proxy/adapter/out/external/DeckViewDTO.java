package com.odevpedro.yugiohcollections.proxy.adapter.out.external;

import lombok.Data;

import java.util.List;

@Data
public class DeckViewDTO {
    private Long id;
    private String ownerId;
    private String name;

    private List<CardSummaryDTO> cards;
    private Integer totalCards;
    private String notes;
}