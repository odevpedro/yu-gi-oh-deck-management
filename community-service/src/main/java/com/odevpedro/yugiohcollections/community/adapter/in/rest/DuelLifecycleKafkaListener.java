package com.odevpedro.yugiohcollections.community.adapter.in.rest;

import com.odevpedro.yugiohcollections.community.application.service.PlayerService;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DuelLifecycleKafkaListener {

    private final PlayerService playerService;

    @KafkaListener(topics = "duel.iniciado", containerFactory = "kafkaListenerContainerFactory")
    public void onDuelStarted(Map<String, Object> event) {
        updateStatus(event, DuelStatus.IN_DUEL);
    }

    @KafkaListener(topics = "duel.encerrado", containerFactory = "kafkaListenerContainerFactory")
    public void onDuelFinished(Map<String, Object> event) {
        updateStatus(event, DuelStatus.AVAILABLE);
    }

    private void updateStatus(Map<String, Object> event, DuelStatus status) {
        try {
            String playerAId = valueAsString(event.get("playerAId"));
            String playerBId = valueAsString(event.get("playerBId"));

            if (playerAId != null) {
                playerService.updateStatus(UUID.fromString(playerAId), status);
            }
            if (playerBId != null) {
                playerService.updateStatus(UUID.fromString(playerBId), status);
            }
        } catch (Exception e) {
            log.warn("Failed to process duel lifecycle event {}: {}", event, e.getMessage());
        }
    }

    private String valueAsString(Object value) {
        return value != null ? String.valueOf(value) : null;
    }
}
