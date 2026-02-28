package com.odevpedro.yugiohcollections.creator.adapter.out.messaging;

import com.odevpedro.yugiohcollections.creator.application.service.CustomCardService;
import com.odevpedro.yugiohcollections.creator.domain.model.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardValidationConsumer {

    private final CustomCardService customCardService;

    @KafkaListener(topics = "${kafka.topics.card-validated}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onCardValidated(CardValidatedEvent event) {
        log.info("Resultado de validação recebido — cardId={} status={}",
                event.getCardId(), event.getStatus());

        CardStatus status = CardStatus.valueOf(event.getStatus());
        customCardService.updateStatus(event.getCardId(), status, event.getRejectReason());
    }
}