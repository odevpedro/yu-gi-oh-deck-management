package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class CardSummaryDTO{
    private Long cardId;
    private String name;
    private String type;
    private String imageUrl;
    private String description;
    private Integer atk;
    private Integer def;
    private Integer level;
    private Integer quantity;
}
