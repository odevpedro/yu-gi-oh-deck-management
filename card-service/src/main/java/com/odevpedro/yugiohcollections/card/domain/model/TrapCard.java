package com.odevpedro.yugiohcollections.card.domain.model;

import com.odevpedro.yugiohcollections.shared.enums.CardType;
import com.odevpedro.yugiohcollections.shared.enums.TrapType;
import lombok.Getter;

import java.util.Optional;

@Getter
public class TrapCard extends Card {

    private String ownerId;
    @Getter
    private TrapType trapType;

    public TrapCard(Long id, String name, String description, String archetype,
                    CardType type, String imageUrl, TrapType trapType, String ownerId) {
        super(id, name, description, archetype, type, imageUrl);
        this.trapType = trapType;
        this.ownerId = ownerId;
    }

    public TrapCard(long id, String name, String desc, String archetype, CardType type, String imageUrl) {
    }

    public static Optional<TrapCard> create(Long id, String name, String description, String archetype,
                                            String imageUrl, TrapType trapType, String ownerId) {

        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .filter(n -> trapType != null)
                .map(n -> new TrapCard(id, name, description, archetype, CardType.TRAP, imageUrl, trapType, ownerId));
    }
}