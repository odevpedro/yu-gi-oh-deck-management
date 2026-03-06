package com.odevpedro.yugiohcollections.community.application.service.Impl;

import com.odevpedro.yugiohcollections.community.application.service.ChallengeService;
import com.odevpedro.yugiohcollections.community.adapter.out.messaging.ChallengeEventPublisher;
import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import com.odevpedro.yugiohcollections.community.domain.model.ChallengeStatus;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.port.ChallengeRepositoryPort;
import com.odevpedro.yugiohcollections.community.domain.port.PlayerRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepositoryPort challengeRepository;
    private final PlayerRepositoryPort playerRepository;
    private final ChallengeEventPublisher eventPublisher;

    @Override
    public Challenge sendChallenge(UUID challengerId, UUID targetId, Long challengerDeckId, String message) {
        var target = playerRepository.findByUserId(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Jogador alvo nao encontrado"));

        if (target.getDuelStatus() != DuelStatus.LOOKING_FOR_DUEL && target.getDuelStatus() != DuelStatus.AVAILABLE) {
            throw new IllegalStateException("Jogador nao esta disponivel para duelo");
        }

        var challenge = challengeRepository.save(Challenge.of(challengerId, targetId, challengerDeckId, message));
        eventPublisher.publishChallengeReceived(challenge);
        return challenge;
    }

    @Override
    public Challenge accept(UUID challengeId, UUID targetId) {
        var challenge = findAndValidate(challengeId, targetId);

        challengeRepository.updateStatus(challengeId, ChallengeStatus.ACCEPTED);
        playerRepository.updateStatus(challenge.getChallengerId(), DuelStatus.IN_DUEL);
        playerRepository.updateStatus(targetId, DuelStatus.IN_DUEL);
        eventPublisher.publishChallengeAccepted(challenge);

        return challengeRepository.findById(challengeId).orElseThrow();
    }

    @Override
    public Challenge decline(UUID challengeId, UUID targetId) {
        var challenge = findAndValidate(challengeId, targetId);
        challengeRepository.updateStatus(challengeId, ChallengeStatus.DECLINED);
        eventPublisher.publishChallengeDeclined(challenge);
        return challengeRepository.findById(challengeId).orElseThrow();
    }

    @Override
    public List<Challenge> findPending(UUID targetId) {
        return challengeRepository.findPendingByTargetId(targetId);
    }

    @Override
    @Scheduled(fixedDelay = 60_000)
    public void expireStale() {
        challengeRepository.findExpired().forEach(challenge -> {
            challengeRepository.updateStatus(challenge.getId(), ChallengeStatus.EXPIRED);
            eventPublisher.publishChallengeExpired(challenge);
        });
    }

    private Challenge findAndValidate(UUID challengeId, UUID targetId) {
        var challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Desafio nao encontrado"));

        if (!challenge.getTargetId().equals(targetId)) {
            throw new IllegalArgumentException("Desafio nao pertence a este jogador");
        }
        if (challenge.getStatus() != ChallengeStatus.PENDING) {
            throw new IllegalStateException("Desafio nao esta mais pendente");
        }
        if (challenge.isExpired()) {
            challengeRepository.updateStatus(challengeId, ChallengeStatus.EXPIRED);
            throw new IllegalStateException("Desafio expirado");
        }
        return challenge;
    }
}
