package com.odevpedro.yugiohcollections.community.adapter.out.persistence;

import com.odevpedro.yugiohcollections.community.adapter.out.persistence.repository.PlayerJpaRepository;
import com.odevpedro.yugiohcollections.community.application.mapper.PlayerMapper;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import com.odevpedro.yugiohcollections.community.domain.model.Player;
import com.odevpedro.yugiohcollections.community.domain.port.PlayerRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerRepositoryAdapter implements PlayerRepositoryPort {

    private final PlayerJpaRepository jpaRepository;
    private final PlayerMapper mapper;

    @Override
    public Player save(Player player) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(player)));
    }

    @Override
    public Optional<Player> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<Player> findNearby(double latitude, double longitude, double radiusKm, DuelStatus status) {
        double radiusMeters = radiusKm * 1000;
        String statusStr = status != null ? status.name() : null;
        return jpaRepository.findNearby(latitude, longitude, radiusMeters, statusStr)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void updateStatus(UUID userId, DuelStatus status) {
        jpaRepository.updateStatus(userId, status);
    }
}
