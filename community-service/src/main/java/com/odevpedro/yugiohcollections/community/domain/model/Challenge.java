package com.odevpedro.yugiohcollections.community.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Challenge {

    private final UUID id;
    private final UUID challengerId;
    private final UUID targetId;
    private final Long challengerDeckId;
    private final String message;
    private final ChallengeStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    public static Challenge of(UUID challengerId, UUID targetId, Long challengerDeckId, String message) {
        return Challenge.builder()
                .challengerId(challengerId)
                .targetId(targetId)
                .challengerDeckId(challengerDeckId)
                .message(message)
                .status(ChallengeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
