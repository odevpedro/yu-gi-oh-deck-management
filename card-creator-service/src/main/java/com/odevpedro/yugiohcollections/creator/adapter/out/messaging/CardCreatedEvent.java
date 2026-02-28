package com.odevpedro.yugiohcollections.creator.adapter.out.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCreatedEvent {
    private Long cardId;
    private String ownerId;
    private String name;
    private String description;
    private String cardType;
    private Integer attack;
    private Integer defense;
    private Integer level;
    private String attribute;
    private String monsterType;
    private String summonCondition;
    private String subType;
}