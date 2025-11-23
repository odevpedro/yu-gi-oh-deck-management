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

    private String name;
    private String description;
    private String archetype;
    private String imageUrl;

    private Integer attack;
    private Integer defense;
    private Integer level;
    private MonsterAttribute attribute;
    private MonsterType monsterType;
    private Set<MonsterSubType> subTypes;

    private SpellType spellType;

    private TrapType trapType;
}