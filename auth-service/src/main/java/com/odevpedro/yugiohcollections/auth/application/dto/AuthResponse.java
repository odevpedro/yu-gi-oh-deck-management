package com.odevpedro.yugiohcollections.auth.application.dto;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        String username,
        String email,
        UUID id
) {
    public AuthResponse(String accessToken, String refreshToken, String username, String email, UUID id) {
        this(accessToken, refreshToken, "Bearer", 3600000L, username, email, id);
    }
}