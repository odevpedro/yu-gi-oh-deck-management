package com.odevpedro.yugiohcollections.card.domain.model;


import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.SpellType;
import lombok.Getter;

import java.util.Optional;

@Getter
public class SpellCard extends Card {


    private  SpellType spellType;
    private String ownerId;

    public SpellCard(Long id, String name, String description, String archetype,
                     CardType type, String imageUrl, SpellType spellType, String ownerId) {
        super(id, name, description, archetype, type, imageUrl);
        this.spellType = spellType;
        this.ownerId = ownerId;
    }

    public SpellCard(Long id, String name, String description, String archetype,
                     CardType type, String imageUrl) {
        super(id, name, description, archetype, type, imageUrl);
    }



    public static Optional<SpellCard> create(Long id, String name, String description, String archetype,
                                             String imageUrl, SpellType spellType, String ownerId) {
        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .filter(n -> spellType != null)
                .map(n -> new SpellCard(id, name, description, archetype, CardType.SPELL, imageUrl, spellType, ownerId));
    }
}