package com.odevpedro.yugiohcollections.domain.model;

import com.odevpedro.yugiohcollections.domain.model.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public abstract class  Card {

    protected final  Long id;
    protected final  String name;
    protected final  String description;
    protected final  String archetype;
    protected final  CardType type;
    protected final  String imageUrl;

}
