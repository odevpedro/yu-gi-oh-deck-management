package com.odevpedro.yugiohcollections.auth.adapter.in.rest;

import com.odevpedro.yugiohcollections.auth.application.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class TokenValidationController {

    private final TokenBlacklistService tokenBlacklistService;

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Map.of("valid", false, "reason", "missing_token"));
        }

        String token = authHeader.substring(7);
        boolean blacklisted = tokenBlacklistService.isTokenBlacklisted(token);

        if (blacklisted) {
            return ResponseEntity.ok(Map.of("valid", false, "reason", "blacklisted"));
        }

        return ResponseEntity.ok(Map.of("valid", true));
    }
}