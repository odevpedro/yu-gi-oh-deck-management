package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity;

import com.odevpedro.yugiohcollections.deck.domain.model.enums.SpellType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "spell_cards")
@Getter
@Setter
@NoArgsConstructor
public class SpellCardEntity extends CardJpaEntity {

    @Enumerated(EnumType.STRING)
    private SpellType spellType;

    @Column(name = "owner_id")
    private String ownerId;

}