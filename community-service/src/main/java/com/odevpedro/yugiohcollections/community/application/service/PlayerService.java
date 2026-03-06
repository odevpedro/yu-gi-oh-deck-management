package com.odevpedro.yugiohcollections.community.application.service;

import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.model.Player;

import java.util.List;
import java.util.UUID;

public interface PlayerService {

    Player registerOrUpdate(UUID userId, String displayName, double latitude, double longitude, java.util.List<String> platforms);

    void updateStatus(UUID userId, DuelStatus status);

    List<Player> findNearby(double latitude, double longitude, double radiusKm, DuelStatus status);
}
