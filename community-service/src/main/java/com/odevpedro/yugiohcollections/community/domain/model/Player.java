package com.odevpedro.yugiohcollections.community.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class Player {

    private final UUID id;
    private final UUID userId;
    private final String displayName;
    private final double latitude;
    private final double longitude;
    private final List<String> platforms;
    private final DuelStatus duelStatus;
    private final LocalDateTime updatedAt;

    public static Player of(UUID userId, String displayName, double latitude, double longitude, List<String> platforms) {
        return Player.builder()
                .userId(userId)
                .displayName(displayName)
                .latitude(latitude)
                .longitude(longitude)
                .platforms(platforms)
                .duelStatus(DuelStatus.AVAILABLE)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
