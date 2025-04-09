package com.odevpedro.yugiohcollections.domain.model;

import com.odevpedro.yugiohcollections.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.domain.model.enums.TrapType;

import java.util.Optional;

public class TrapCard extends Card {
    private final TrapType trapType;

    public TrapCard(Long id, String name, String description, String archetype,
                    CardType type, String imageUrl, TrapType trapType) {
        super(id, name, description, archetype, type, imageUrl);
        this.trapType = trapType;
    }

    public static Optional<TrapCard> create(Long id, String name, String description, String archetype,
                                            String imageUrl, TrapType trapType) {

        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .filter(n -> trapType != null)
                .map(n -> new TrapCard(id, name, description, archetype,
                        CardType.TRAP, imageUrl, trapType));
    }

}
