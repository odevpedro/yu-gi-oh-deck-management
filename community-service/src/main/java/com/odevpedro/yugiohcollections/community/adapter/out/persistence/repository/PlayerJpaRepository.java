package com.odevpedro.yugiohcollections.community.adapter.out.persistence.repository;

import com.odevpedro.yugiohcollections.community.adapter.out.persistence.entity.PlayerEntity;
import com.odevpedro.yugiohcollections.community.domain.model.DuelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, UUID> {

    Optional<PlayerEntity> findByUserId(UUID userId);

    @Query(value = """
    SELECT p.* FROM players p
    WHERE (:status IS NULL OR p.duel_status = :status)
    AND ST_DWithin(
        CAST(p.location AS geography),
        CAST(ST_MakePoint(:lng, :lat) AS geography),
        :radiusMeters
    )
    ORDER BY ST_Distance(
        CAST(p.location AS geography),
        CAST(ST_MakePoint(:lng, :lat) AS geography)
    )
    """, nativeQuery = true)
    List<PlayerEntity> findNearby(@Param("lat") double lat,
                                  @Param("lng") double lng,
                                  @Param("radiusMeters") double radiusMeters,
                                  @Param("status") String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PlayerEntity p SET p.duelStatus = :status WHERE p.userId = :userId")
    void updateStatus(@Param("userId") UUID userId, @Param("status") DuelStatus status);
}
