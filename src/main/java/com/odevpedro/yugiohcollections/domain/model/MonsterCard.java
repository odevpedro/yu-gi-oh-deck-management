package com.odevpedro.yugiohcollections.domain.model;

import com.odevpedro.yugiohcollections.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.domain.model.enums.MonsterType;

import java.util.Set;

public class MonsterCard {
    private int attack;
    private int defense;
    private int level;

    private MonsterAttribute attribute; // LIGHT, DARK, etc.

    private MonsterType monsterType; // Dragon, Warrior, etc.

    private Set<MonsterSubType> subTypes; // Effect, Fusion, Ritual...

}
