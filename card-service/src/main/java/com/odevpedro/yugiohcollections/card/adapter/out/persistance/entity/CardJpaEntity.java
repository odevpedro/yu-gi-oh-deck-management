package com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class CardJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ok para H2
    @Column(name = "id")
    private Long id;

    @Column(name="name", nullable=false) private String name;
    @Column(name="description", length=1000) private String description;
    @Column(name="archetype") private String archetype;
    @Column(name="image_url") private String imageUrl;
    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable=false) private CardType type;
}
