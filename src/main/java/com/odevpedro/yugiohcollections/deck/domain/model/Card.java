package com.odevpedro.yugiohcollections.deck.domain.model;

import com.odevpedro.yugiohcollections.deck.domain.model.enums.CardType;

public abstract class Card {

    protected final Long id;
    protected final String name;
    protected final String description;
    protected final String archetype;
    protected final CardType type;
    protected final String imageUrl;

    protected Card(Long id, String name, String description, String archetype, CardType type, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.archetype = archetype;
        this.type = type;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getArchetype() { return archetype; }
    public CardType getType() { return type; }
    public String getImageUrl() { return imageUrl; }
}