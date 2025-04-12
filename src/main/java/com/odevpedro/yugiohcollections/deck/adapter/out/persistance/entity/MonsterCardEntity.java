package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity;

import com.odevpedro.yugiohcollections.deck.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.deck.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.deck.domain.model.enums.MonsterType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "monster_cards")
@Getter
@Setter
@NoArgsConstructor
public class MonsterCardEntity extends CardJpaEntity {



    private int attack;
    private int defense;
    private int level;

    @Enumerated(EnumType.STRING)
    private MonsterAttribute attribute;

    @Enumerated(EnumType.STRING)
    private MonsterType monsterType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "monster_card_subtypes", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "sub_type")
    @Enumerated(EnumType.STRING)
    private Set<MonsterSubType> subTypes;
}