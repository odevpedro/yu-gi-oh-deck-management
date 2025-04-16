package com.odevpedro.yugiohcollections.card.application.mapper;

import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.MonsterCardEntity;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.SpellCardEntity;
import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.TrapCardEntity;
import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import com.odevpedro.yugiohcollections.card.domain.model.SpellCard;
import com.odevpedro.yugiohcollections.card.domain.model.TrapCard;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public MonsterCardEntity toEntity(MonsterCard card) {
        MonsterCardEntity entity = new MonsterCardEntity();
        entity.setId(card.getId());
        entity.setName(card.getName());
        entity.setDescription(card.getDescription());
        entity.setArchetype(card.getArchetype());
        entity.setType(card.getType());
        entity.setImageUrl(card.getImageUrl());
        entity.setAttack(card.getAttack());
        entity.setDefense(card.getDefense());
        entity.setLevel(card.getLevel());
        entity.setAttribute(card.getAttribute());
        entity.setMonsterType(card.getMonsterType());
        entity.setSubTypes(card.getSubTypes());
        entity.setOwnerId(card.getOwnerId());
        return entity;
    }

    public MonsterCard toDomain(MonsterCardEntity entity) {
        return new MonsterCard(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getArchetype(),
                entity.getType(),
                entity.getImageUrl(),
                entity.getAttack(),
                entity.getDefense(),
                entity.getLevel(),
                entity.getAttribute(),
                entity.getMonsterType(),
                entity.getSubTypes(),
                entity.getOwnerId()
        );
    }

    public SpellCardEntity toEntity(SpellCard card) {
        SpellCardEntity entity = new SpellCardEntity();
        entity.setId(card.getId());
        entity.setName(card.getName());
        entity.setDescription(card.getDescription());
        entity.setArchetype(card.getArchetype());
        entity.setType(card.getType());
        entity.setImageUrl(card.getImageUrl());
        entity.setSpellType(card.getSpellType());
        entity.setOwnerId(card.getOwnerId());
        return entity;
    }

    public SpellCard toDomain(SpellCardEntity entity) {
        return new SpellCard(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getArchetype(),
                entity.getType(),
                entity.getImageUrl(),
                entity.getSpellType(),
                entity.getOwnerId()
        );
    }

    public TrapCardEntity toEntity(TrapCard card) {
        TrapCardEntity entity = new TrapCardEntity();
        entity.setId(card.getId());
        entity.setName(card.getName());
        entity.setDescription(card.getDescription());
        entity.setArchetype(card.getArchetype());
        entity.setType(card.getType());
        entity.setImageUrl(card.getImageUrl());
        entity.setTrapType(card.getTrapType());
        entity.setOwnerId(card.getOwnerId());
        return entity;
    }

    public TrapCard toDomain(TrapCardEntity entity) {
        return new TrapCard(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getArchetype(),
                entity.getType(),
                entity.getImageUrl(),
                entity.getTrapType(),
                entity.getOwnerId()
        );
    }
}
