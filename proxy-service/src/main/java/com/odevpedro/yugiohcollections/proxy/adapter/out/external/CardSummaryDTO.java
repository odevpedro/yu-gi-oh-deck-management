package com.odevpedro.yugiohcollections.proxy.adapter.out.external;

import lombok.Data;

@Data
public class CardSummaryDTO {
    private Long cardId;
    private String name;
    private String imageUrl;
    private int quantity;
}