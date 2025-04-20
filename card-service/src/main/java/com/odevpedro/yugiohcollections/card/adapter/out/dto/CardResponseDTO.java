package com.odevpedro.yugiohcollections.card.adapter.out.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardResponseDTO {
    private Long id;
    private String name;
    private String type;
    private String imageUrl;
}
