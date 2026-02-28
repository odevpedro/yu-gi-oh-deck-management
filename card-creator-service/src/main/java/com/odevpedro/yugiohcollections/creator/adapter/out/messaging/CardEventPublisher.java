package com.odevpedro.yugiohcollections.creator.adapter.out.messaging;

import com.odevpedro.yugiohcollections.creator.domain.model.CustomCard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.card-created}")
    private String cardCreatedTopic;

    public void publishCardCreated(CustomCard card) {
        CardCreatedEvent event = new CardCreatedEvent(
                card.getId(),
                card.getOwnerId(),
                card.getName(),
                card.getDescription(),
                card.getCardType().name(),
                card.getAttack(),
                card.getDefense(),
                card.getLevel(),
                card.getAttribute() != null ? card.getAttribute().name() : null,
                card.getMonsterType(),
                card.getSummonCondition(),
                card.getSubType()
        );
        kafkaTemplate.send(cardCreatedTopic, String.valueOf(card.getId()), event);
    }
}