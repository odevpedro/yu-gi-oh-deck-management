package com.odevpedro.yugiohcollections.deck.application.dto;

import com.odevpedro.yugiohcollections.deck.domain.model.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/*Essa classe precisa incluir os campos de todos os tipos possiveis,
* por isso existem os tipos comuns e os especificos de cada.*/
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