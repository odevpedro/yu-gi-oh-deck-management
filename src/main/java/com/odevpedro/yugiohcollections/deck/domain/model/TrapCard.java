package com.odevpedro.yugiohcollections.deck.domain.model;

import com.odevpedro.yugiohcollections.deck.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.deck.domain.model.enums.TrapType;
import lombok.Getter;

import java.util.Optional;

@Getter
public class TrapCard extends Card {

    private final String ownerId;
    @Getter
    private final TrapType trapType;

    public TrapCard(Long id, String name, String description, String archetype,
                    CardType type, String imageUrl, TrapType trapType, String ownerId) {
        super(id, name, description, archetype, type, imageUrl);
        this.trapType = trapType;
        this.ownerId = ownerId;
    }

    public static Optional<TrapCard> create(Long id, String name, String description, String archetype,
                                            String imageUrl, TrapType trapType, String ownerId) {

        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .filter(n -> trapType != null)
                .map(n -> new TrapCard(id, name, description, archetype, CardType.TRAP, imageUrl, trapType, ownerId));
    }
}