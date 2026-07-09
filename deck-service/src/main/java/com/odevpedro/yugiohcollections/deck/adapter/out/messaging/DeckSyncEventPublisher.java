package com.odevpedro.yugiohcollections.deck.adapter.out.messaging;

import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeckSyncEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.deck-synced:deck.synced}")
    private String deckSyncedTopic;

    public void publish(String action, Deck deck) {
        DeckSyncEvent event = new DeckSyncEvent(
                deck.getId(),
                deck.getOwnerId(),
                deck.getName(),
                action,
                deck.getMainDeck(),
                deck.getExtraDeck(),
                deck.getSideDeck(),
                Instant.now()
        );

        try {
            kafkaTemplate.send(deckSyncedTopic, String.valueOf(deck.getId()), event);
        } catch (Exception ex) {
            log.warn("Nao foi possivel publicar evento de deck {}: {}", action, ex.getMessage());
        }
    }
}
