package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity;

import com.odevpedro.yugiohcollections.deck.domain.model.enums.SpellType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
}