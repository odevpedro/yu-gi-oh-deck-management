package com.odevpedro.yugiohcollections.card.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardSimpleResponse {
    private Long id;
    private String name;
    private String type;
    private String imageUrl;
}