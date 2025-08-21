package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CardResponseDTO {
    private Long id;
    private String name;
    private String type;      // "MONSTER" | "SPELL" | "TRAP"
    private String imageUrl;
    private String description; // inclua/retire conforme o retorno real
}