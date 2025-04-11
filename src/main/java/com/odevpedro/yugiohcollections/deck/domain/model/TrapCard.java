package com.odevpedro.yugiohcollections.deck.domain.model;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.deck.domain.model.enums.TrapType;
import lombok.Getter;

import java.util.Optional;

@Getter
public class TrapCard extends Card {

    private final TrapType trapType;

    public TrapCard(Long id, String name, String description, String archetype,
                    CardType type, String imageUrl, TrapType trapType) {
        super(id, name, description, archetype, type, imageUrl);
        this.trapType = trapType;
    }

    public TrapType getTrapType() {
        return trapType;
    }

    public static Optional<TrapCard> create(Long id, String name, String description, String archetype,
                                            String imageUrl, TrapType trapType) {

        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .filter(n -> trapType != null)
                .map(n -> new TrapCard(id, name, description, archetype, CardType.TRAP, imageUrl, trapType));
    }
}