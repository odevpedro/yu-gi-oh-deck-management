package com.odevpedro.yugiohcollections.model;

import com.odevpedro.yugiohcollections.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.domain.model.enums.MonsterSubType;
import com.odevpedro.yugiohcollections.domain.model.enums.MonsterType;

import java.util.Set;

public abstract class MosterCard {
    private int attack;
    private int defense;
    private int level;

    private MonsterAttribute attribute; // LIGHT, DARK, etc.
    private MonsterType monsterType; // Dragon, Warrior, etc.
    private Set<MonsterSubType> subTypes; // Effect, Fusion, Ritual...
}
