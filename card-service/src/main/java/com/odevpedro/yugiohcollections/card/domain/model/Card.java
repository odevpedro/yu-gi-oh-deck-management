package com.odevpedro.yugiohcollections.card.domain.model;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class Card {

    protected final Long id;
    protected final String name;
    protected final String description;
    protected final String archetype;
    protected final CardType type;
    protected final String imageUrl;

}