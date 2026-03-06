package com.odevpedro.yugiohcollections.community.domain.port;

import com.odevpedro.yugiohcollections.community.domain.model.Challenge;
import com.odevpedro.yugiohcollections.community.domain.model.ChallengeStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChallengeRepositoryPort {

    Challenge save(Challenge challenge);

    Optional<Challenge> findById(UUID id);

    List<Challenge> findPendingByTargetId(UUID targetId);

    List<Challenge> findExpired();

    void updateStatus(UUID id, ChallengeStatus status);
}
