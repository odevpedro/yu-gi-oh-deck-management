package com.odevpedro.yugiohcollections.community.adapter.out.persistence.repository;

import com.odevpedro.yugiohcollections.community.adapter.out.persistence.entity.ChallengeEntity;
import com.odevpedro.yugiohcollections.community.domain.model.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChallengeJpaRepository extends JpaRepository<ChallengeEntity, UUID> {

    List<ChallengeEntity> findByTargetIdAndStatus(UUID targetId, ChallengeStatus status);

    @Query("SELECT c FROM ChallengeEntity c WHERE c.status = 'PENDING' AND c.expiresAt < :now")
    List<ChallengeEntity> findExpired(@Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ChallengeEntity c SET c.status = :status WHERE c.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") ChallengeStatus status);
}
