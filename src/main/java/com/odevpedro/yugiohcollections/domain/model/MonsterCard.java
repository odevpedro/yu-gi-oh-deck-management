package com.odevpedro.yugiohcollections.domain.model;

import com.odevpedro.yugiohcollections.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.domain.model.enums.MonsterType;
import lombok.Getter;

import java.util.Optional;
import java.util.Set;

@Getter
public class MonsterCard extends Card{
    private final int attack;
    private final int defense;
    private final int level;
    private final MonsterAttribute attribute;
    private final MonsterType monsterType;
    private final Set<MonsterSubType> subTypes;

    public MonsterCard(Long id, String name, String description, String archetype, CardType type, String imageUrl, int attack, int defense, int level, MonsterAttribute attribute, MonsterType monsterType, Set<MonsterSubType> subTypes) {
        super(id, name, description, archetype, type, imageUrl);
        this.attack = attack;
        this.defense = defense;
        this.level = level;
        this.attribute = attribute;
        this.monsterType = monsterType;
        this.subTypes = subTypes;
    }



    //padrão factory funcional/declarativo
    public static Optional<MonsterCard>
    create(Long id, String name, String description, String archetype, String imageUrl,
           int attack, int defense, int level,
           MonsterAttribute attribute, MonsterType monsterType,
           Set<MonsterSubType> subTypes) {

        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .filter(n -> level >= 1 && level <= 12)
                .filter(n -> attack >= 0 && defense >= 0)
                .filter(n -> attribute != null && monsterType != null && subTypes != null)
                .map(n -> new MonsterCard(
                        id, name, description, archetype,
                        CardType.MONSTER, imageUrl,
                        attack, defense, level, attribute, monsterType, subTypes));
    }

}
