package com.odevpedro.yugiohcollections.creator.application.dto;

import com.odevpedro.yugiohcollections.shared.enums.MonsterAttribute;

public record CreateCardRequest(
        String cardType,       // MONSTER | SPELL | TRAP

        // Comum
        String name,
        String description,

        // Monstro
        Integer attack,
        Integer defense,
        Integer level,
        MonsterAttribute attribute,
        String monsterType,
        String summonCondition,

        // Spell / Trap
        String subType
) {}