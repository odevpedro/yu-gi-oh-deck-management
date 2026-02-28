package com.odevpedro.yugiohcollections.creator.application.mapper;

import com.odevpedro.yugiohcollections.creator.adapter.out.persistence.entity.CustomCardEntity;
import com.odevpedro.yugiohcollections.creator.domain.model.CustomCard;
import org.springframework.stereotype.Component;

@Component
public class CustomCardMapper {

    public CustomCardEntity toEntity(CustomCard card) {
        return CustomCardEntity.builder()
                .id(card.getId())
                .ownerId(card.getOwnerId())
                .name(card.getName())
                .description(card.getDescription())
                .cardType(card.getCardType())
                .status(card.getStatus())
                .rejectReason(card.getRejectReason())
                .attack(card.getAttack())
                .defense(card.getDefense())
                .level(card.getLevel())
                .attribute(card.getAttribute())
                .monsterType(card.getMonsterType())
                .summonCondition(card.getSummonCondition())
                .subType(card.getSubType())
                .build();
    }

    public CustomCard toDomain(CustomCardEntity entity) {
        return CustomCard.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .name(entity.getName())
                .description(entity.getDescription())
                .cardType(entity.getCardType())
                .status(entity.getStatus())
                .rejectReason(entity.getRejectReason())
                .attack(entity.getAttack())
                .defense(entity.getDefense())
                .level(entity.getLevel())
                .attribute(entity.getAttribute())
                .monsterType(entity.getMonsterType())
                .summonCondition(entity.getSummonCondition())
                .subType(entity.getSubType())
                .build();
    }
}