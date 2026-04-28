package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.TokenBlacklistEntity;
import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtService jwtService;

    @Transactional
    public void blacklistToken(String token) {
        String tokenHash = RefreshTokenService.hashToken(token);

        if (tokenBlacklistRepository.existsByTokenHash(tokenHash)) {
            return;
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtService.getSecret().getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        LocalDateTime expiresAt = expiration.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        TokenBlacklistEntity blacklistEntry = TokenBlacklistEntity.builder()
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .blacklistedAt(LocalDateTime.now())
                .build();

        tokenBlacklistRepository.save(blacklistEntry);
    }

    public boolean isTokenBlacklisted(String token) {
        String tokenHash = RefreshTokenService.hashToken(token);
        return tokenBlacklistRepository.existsByTokenHash(tokenHash);
    }

    @Transactional
    public void cleanupExpiredEntries() {
        tokenBlacklistRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}