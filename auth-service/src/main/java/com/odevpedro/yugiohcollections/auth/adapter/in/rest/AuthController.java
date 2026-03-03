package com.odevpedro.yugiohcollections.auth.adapter.in.rest;

import com.odevpedro.yugiohcollections.auth.application.dto.AuthResponse;
import com.odevpedro.yugiohcollections.auth.application.dto.LoginRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RegisterRequest;
import com.odevpedro.yugiohcollections.auth.application.service.AuthService;
import com.odevpedro.yugiohcollections.auth.application.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(Map.of(
                "userId", jwtService.extractUserId(token),
                "username", jwtService.extractUsername(token)
        ));
    }
}
