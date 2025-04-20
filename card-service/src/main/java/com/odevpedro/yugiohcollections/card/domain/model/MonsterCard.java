package com.odevpedro.yugiohcollections.card.domain.model;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterType;

import java.util.Optional;
import java.util.Set;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonsterCard extends Card {

    private String ownerId;
    private final int attack;
    private final int defense;
    private final int level;
    private final MonsterAttribute attribute;
    private final MonsterType monsterType;
    private final Set<MonsterSubType> subTypes;

    public MonsterCard(Long id, String name, String description, String archetype, CardType type,
                       String imageUrl, int attack, int defense, int level,
                       MonsterAttribute attribute, MonsterType monsterType,
                       Set<MonsterSubType> subTypes, String ownerId) {
        super(id, name, description, archetype, type, imageUrl);
        this.attack = attack;
        this.defense = defense;
        this.level = level;
        this.attribute = attribute;
        this.monsterType = monsterType;
        this.subTypes = subTypes;
        this.ownerId = ownerId;
    }

    public static Optional<MonsterCard> create(Long id, String name, String description, String archetype,
                                               String imageUrl, int attack, int defense, int level,
                                               MonsterAttribute attribute, MonsterType monsterType,
                                               Set<MonsterSubType> subTypes, String ownerId) {
        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .filter(n -> level >= 1 && level <= 12)
                .filter(n -> attribute != null && monsterType != null && subTypes != null)
                .map(n -> new MonsterCard(id, name, description, archetype, CardType.MONSTER, imageUrl,
                        attack, defense, level, attribute, monsterType, subTypes, ownerId));
    }
}