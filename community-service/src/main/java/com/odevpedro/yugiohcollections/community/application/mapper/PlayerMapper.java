package com.odevpedro.yugiohcollections.community.application.mapper;

import com.odevpedro.yugiohcollections.community.adapter.out.persistence.entity.PlayerEntity;
import com.odevpedro.yugiohcollections.community.domain.model.Player;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    public PlayerEntity toEntity(Player player) {
        var point = GF.createPoint(new Coordinate(player.getLongitude(), player.getLatitude()));
        return PlayerEntity.builder()
                .id(player.getId())
                .userId(player.getUserId())
                .displayName(player.getDisplayName())
                .location(point)
                .platforms(player.getPlatforms())
                .duelStatus(player.getDuelStatus())
                .updatedAt(player.getUpdatedAt())
                .build();
    }

    public Player toDomain(PlayerEntity entity) {
        return Player.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .displayName(entity.getDisplayName())
                .latitude(entity.getLocation().getY())
                .longitude(entity.getLocation().getX())
                .platforms(entity.getPlatforms())
                .duelStatus(entity.getDuelStatus())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
