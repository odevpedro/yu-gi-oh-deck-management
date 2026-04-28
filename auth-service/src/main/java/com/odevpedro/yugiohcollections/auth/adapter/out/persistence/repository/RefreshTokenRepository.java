package com.odevpedro.yugiohcollections.auth.adapter.out.persistence.repository;

import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenAndRevokedFalse(String token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.user.id = :userId")
    void revokeAllByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}