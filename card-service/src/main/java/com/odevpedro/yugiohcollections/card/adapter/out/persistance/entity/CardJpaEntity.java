package com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class CardJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String archetype;
    @Enumerated(EnumType.STRING)
    private CardType type;
    private String imageUrl;

}