package com.odevpedro.yugiohcollections.auth.adapter.in.rest;

import com.odevpedro.yugiohcollections.auth.application.dto.AuthResponse;
import com.odevpedro.yugiohcollections.auth.application.dto.LoginRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RefreshTokenRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RegisterRequest;
import com.odevpedro.yugiohcollections.auth.application.service.AuthService;
import com.odevpedro.yugiohcollections.auth.application.service.JwtService;
import com.odevpedro.yugiohcollections.shared.constants.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(ApiRoutes.AUTH_BASE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping(ApiRoutes.AUTH_REGISTER)
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping(ApiRoutes.AUTH_LOGIN)
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiRoutes.AUTH_ME)
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(Map.of(
                "userId", jwtService.extractUserId(token),
                "username", jwtService.extractUsername(token)
        ));
    }
}
