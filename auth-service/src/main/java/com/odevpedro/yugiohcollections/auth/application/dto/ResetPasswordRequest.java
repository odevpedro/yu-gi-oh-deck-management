package com.odevpedro.yugiohcollections.auth.application.dto;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {}
