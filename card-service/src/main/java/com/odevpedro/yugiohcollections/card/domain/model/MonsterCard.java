package com.odevpedro.yugiohcollections.card.domain.model;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterType;

import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonsterCard extends Card {

    private String ownerId;
    private int attack;
    private int defense;
    private  int level;
    private MonsterAttribute attribute;
    private  MonsterType monsterType;
    private  Set<MonsterSubType> subTypes;

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

    public MonsterCard(Long id, String name, String description, String archetype,
                       CardType type, String imageUrl) {
        super(id, name, description, archetype, type, imageUrl);
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