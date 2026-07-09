package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("yugioh-deck-management-super-secret-key-256bits-minimum");
        properties.setExpirationMs(3600000L);
        properties.setRefreshExpirationMs(604800000L);
        jwtService = new JwtService(properties);
    }

    @Test
    void shouldGenerateAndParseAccessToken() {
        UUID userId = UUID.randomUUID();

        String token = jwtService.generateToken(userId, "yugi", "USER");

        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtService.extractUsername(token)).isEqualTo("yugi");
        assertThat(jwtService.extractRole(token)).isEqualTo("USER");
        assertThat(jwtService.isRefreshToken(token)).isFalse();
        assertThat(jwtService.isTokenExpired(token)).isFalse();
    }

    @Test
    void shouldGenerateRefreshTokenMarkedAsRefresh() {
        UUID userId = UUID.randomUUID();

        String token = jwtService.generateRefreshToken(userId, "yugi", "USER");

        assertThat(jwtService.isRefreshToken(token)).isTrue();
    }
}
