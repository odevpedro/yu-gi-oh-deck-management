package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class CardJpaEntity {

    @Id
    private Long id;

    private String name;
    private String description;
    private String archetype;
    @Enumerated(EnumType.STRING)
    private com.odevpedro.yugiohcollections.card.domain.model.enums.CardType type;
    private String imageUrl;

}