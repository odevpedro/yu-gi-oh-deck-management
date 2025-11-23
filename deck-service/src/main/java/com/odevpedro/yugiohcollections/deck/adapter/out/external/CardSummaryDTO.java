package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class CardSummaryDTO{
    private Long cardId;
    String name;
    String type;
    String imageUrl;
    String description;
    Integer quantity;
}