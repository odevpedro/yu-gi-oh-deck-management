package com.odevpedro.yugiohcollections.card.application.dto;

import com.odevpedro.yugiohcollections.card.domain.model.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
public class CardInputDTO {

    private String ownerId;


    private CardType type;

    // Comuns a todas as cartas
    private String name;
    private String description;
    private String archetype;
    private String imageUrl;

    // Específicos de Monster
    private Integer attack;
    private Integer defense;
    private Integer level;
    private MonsterAttribute attribute;
    private MonsterType monsterType;
    private Set<MonsterSubType> subTypes;

    // Específico de Spell
    private SpellType spellType;

    // Específico de Trap
    private TrapType trapType;
}