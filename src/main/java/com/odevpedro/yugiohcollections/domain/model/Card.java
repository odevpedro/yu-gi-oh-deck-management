package com.odevpedro.yugiohcollections.domain.model;

import com.odevpedro.yugiohcollections.domain.model.enums.CardType;
import jakarta.persistence.*;

@Entity

public abstract class  Card {

    private Long id;
    private String name;
    private String description;
    private String archetype;
    private CardType type; // MONSTER, SPELL, TRAP
    private String imageUrl;




}
