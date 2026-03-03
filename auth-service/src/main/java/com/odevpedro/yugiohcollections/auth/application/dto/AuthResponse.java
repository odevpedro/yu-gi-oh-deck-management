package com.odevpedro.yugiohcollections.auth.application.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        String username,
        String email,
        UUID id
) {}