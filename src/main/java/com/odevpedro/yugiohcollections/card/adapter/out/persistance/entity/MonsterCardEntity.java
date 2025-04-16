package com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity;

import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "monster_cards")
@Data
@NoArgsConstructor
public class MonsterCardEntity extends CardJpaEntity {

    @Column(name = "owner_id")
    private String ownerId;

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