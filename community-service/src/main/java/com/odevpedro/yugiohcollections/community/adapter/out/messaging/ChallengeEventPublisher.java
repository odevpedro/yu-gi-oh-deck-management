package com.odevpedro.yugiohcollections.community.adapter.out.messaging;

import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeEventPublisher {

    private static final String TOPIC_RECEIVED = "desafio.recebido";
    private static final String TOPIC_ACCEPTED  = "desafio.aceito";
    private static final String TOPIC_DECLINED  = "desafio.recusado";
    private static final String TOPIC_EXPIRED   = "desafio.expirado";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishChallengeReceived(Challenge challenge) {
        log.info("Publicando desafio.recebido para targetId={}", challenge.getTargetId());
        kafkaTemplate.send(TOPIC_RECEIVED, challenge.getId().toString(), challenge);
    }

    public void publishChallengeAccepted(Challenge challenge) {
        log.info("Publicando desafio.aceito challengeId={}", challenge.getId());
        kafkaTemplate.send(TOPIC_ACCEPTED, challenge.getId().toString(), challenge);
    }

    public void publishChallengeDeclined(Challenge challenge) {
        log.info("Publicando desafio.recusado challengeId={}", challenge.getId());
        kafkaTemplate.send(TOPIC_DECLINED, challenge.getId().toString(), challenge);
    }

    public void publishChallengeExpired(Challenge challenge) {
        log.info("Publicando desafio.expirado challengeId={}", challenge.getId());
        kafkaTemplate.send(TOPIC_EXPIRED, challenge.getId().toString(), challenge);
    }
}
