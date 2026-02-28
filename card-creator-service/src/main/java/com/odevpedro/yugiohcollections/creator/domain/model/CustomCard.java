package com.odevpedro.yugiohcollections.creator.domain.model;

import com.odevpedro.yugiohcollections.creator.domain.model.enums.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomCard {

    private Long id;
    private String ownerId;
    private String name;
    private String description;
    private CardType cardType;
    private CardStatus status;
    private String rejectReason;

    // Monstro
    private Integer attack;
    private Integer defense;
    private Integer level;
    private MonsterAttribute attribute;
    private String monsterType;
    private String summonCondition;

    // Spell / Trap
    private String subType;

    // ===== Factory methods com validação =====

    public static CustomCard createMonster(String ownerId, String name, String description,
                                           Integer attack, Integer defense, Integer level,
                                           MonsterAttribute attribute, String monsterType,
                                           String summonCondition) {
        validateCommon(name, description);
        validateMonster(attack, defense, level, attribute, monsterType);

        return CustomCard.builder()
                .ownerId(ownerId)
                .name(name)
                .description(description)
                .cardType(CardType.MONSTER)
                .status(CardStatus.PENDING)
                .attack(attack)
                .defense(defense)
                .level(level)
                .attribute(attribute)
                .monsterType(monsterType)
                .summonCondition(summonCondition)
                .build();
    }

    public static CustomCard createSpell(String ownerId, String name, String description,
                                         SpellSubType subType) {
        validateCommon(name, description);
        if (subType == null) throw new CardCreationException("Subtipo da Spell é obrigatório");

        return CustomCard.builder()
                .ownerId(ownerId)
                .name(name)
                .description(description)
                .cardType(CardType.SPELL)
                .status(CardStatus.PENDING)
                .subType(subType.name())
                .build();
    }

    public static CustomCard createTrap(String ownerId, String name, String description,
                                        TrapSubType subType) {
        validateCommon(name, description);
        if (subType == null) throw new CardCreationException("Subtipo da Trap é obrigatório");

        return CustomCard.builder()
                .ownerId(ownerId)
                .name(name)
                .description(description)
                .cardType(CardType.TRAP)
                .status(CardStatus.PENDING)
                .subType(subType.name())
                .build();
    }

    // ===== Validações de domínio =====

    private static void validateCommon(String name, String description) {
        if (name == null || name.isBlank())
            throw new CardCreationException("Nome da carta é obrigatório");
        if (name.length() > 255)
            throw new CardCreationException("Nome não pode ter mais de 255 caracteres");
        if (description == null || description.isBlank())
            throw new CardCreationException("Descrição/efeito da carta é obrigatório");
        if (description.length() > 2000)
            throw new CardCreationException("Descrição não pode ter mais de 2000 caracteres");
    }

    private static void validateMonster(Integer attack, Integer defense, Integer level,
                                        MonsterAttribute attribute, String monsterType) {
        if (attack == null || attack < 0 || attack > 5000)
            throw new CardCreationException("ATK deve ser entre 0 e 5000");
        if (defense == null || defense < 0 || defense > 5000)
            throw new CardCreationException("DEF deve ser entre 0 e 5000");
        if (level == null || level < 1 || level > 12)
            throw new CardCreationException("Level deve ser entre 1 e 12");
        if (attribute == null)
            throw new CardCreationException("Atributo do monstro é obrigatório");
        if (monsterType == null || monsterType.isBlank())
            throw new CardCreationException("Tipo do monstro é obrigatório");
    }
}