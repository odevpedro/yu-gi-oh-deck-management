package com.odevpedro.yugiohcollections.auth.adapter.out.persistence.repository;

import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.TokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, Long> {

    boolean existsByTokenHash(String tokenHash);

    @Modifying
    @Query("DELETE FROM TokenBlacklistEntity t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}