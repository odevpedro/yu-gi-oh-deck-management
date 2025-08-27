package com.odevpedro.yugiohcollections.card.domain.model;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Card {

    protected Long id;
    protected String name;
    protected String description;
    protected String archetype;
    protected CardType type;
    protected String imageUrl;
}