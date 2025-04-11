package com.odevpedro.yugiohcollections.deck.domain.model;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.SpellType;

import java.util.Optional;


public class SpellCard extends Card {

    private final SpellType spellType;

    public SpellCard(Long id, String name, String description, String archetype,
                     CardType type, String imageUrl, SpellType spellType) {
        super(id, name, description, archetype, type, imageUrl);
        this.spellType = spellType;
    }

    public static Optional<SpellCard> create(Long id, String name, String description, String archetype,
                                             String imageUrl, SpellType spellType) {

        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .filter(n -> spellType != null)
                .map(n -> new SpellCard(id, name, description, archetype, CardType.SPELL, imageUrl, spellType));
    }

    public SpellType getSpellType() { return spellType; }
}