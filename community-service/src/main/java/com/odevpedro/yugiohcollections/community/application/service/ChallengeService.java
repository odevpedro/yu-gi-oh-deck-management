package com.odevpedro.yugiohcollections.community.application.service;

import com.odevpedro.yugiohcollections.community.domain.model.Challenge;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {

    Challenge sendChallenge(UUID challengerId, UUID targetId, Long challengerDeckId, String message);

    AcceptedChallenge accept(UUID challengeId, UUID targetId, Long targetDeckId);

    Challenge decline(UUID challengeId, UUID targetId);

    List<Challenge> findPending(UUID targetId);

    void expireStale();

    record AcceptedChallenge(Challenge challenge, String duelId) {}
}
