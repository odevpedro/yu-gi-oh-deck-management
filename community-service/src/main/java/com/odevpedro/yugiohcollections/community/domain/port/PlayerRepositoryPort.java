package com.odevpedro.yugiohcollections.community.domain.port;

import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.model.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepositoryPort {

    Player save(Player player);

    Optional<Player> findByUserId(UUID userId);

    List<Player> findNearby(double latitude, double longitude, double radiusKm, DuelStatus status);

    void updateStatus(UUID userId, DuelStatus status);
}
