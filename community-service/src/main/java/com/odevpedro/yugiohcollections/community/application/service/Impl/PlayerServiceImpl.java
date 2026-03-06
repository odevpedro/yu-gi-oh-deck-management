package com.odevpedro.yugiohcollections.community.application.service.Impl;

import com.odevpedro.yugiohcollections.community.application.service.PlayerService;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.model.Player;
import com.odevpedro.yugiohcollections.community.domain.port.PlayerRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepositoryPort playerRepository;

    @Override
    public Player registerOrUpdate(UUID userId, String displayName, double latitude, double longitude, List<String> platforms) {
        return playerRepository.findByUserId(userId)
                .map(existing -> Player.builder()
                        .id(existing.getId())
                        .userId(userId)
                        .displayName(displayName)
                        .latitude(latitude)
                        .longitude(longitude)
                        .platforms(platforms)
                        .duelStatus(existing.getDuelStatus())
                        .updatedAt(java.time.LocalDateTime.now())
                        .build())
                .map(playerRepository::save)
                .orElseGet(() -> playerRepository.save(Player.of(userId, displayName, latitude, longitude, platforms)));
    }

    @Override
    public void updateStatus(UUID userId, DuelStatus status) {
        playerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Jogador nao encontrado"));
        playerRepository.updateStatus(userId, status);
    }

    @Override
    public List<Player> findNearby(double latitude, double longitude, double radiusKm, DuelStatus status) {
        return playerRepository.findNearby(latitude, longitude, radiusKm, status);
    }
}
