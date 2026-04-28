package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.UserEntity;
import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.repository.UserJpaRepository;
import com.odevpedro.yugiohcollections.auth.application.dto.AuthResponse;
import com.odevpedro.yugiohcollections.auth.application.dto.LoginRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RefreshTokenRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RegisterRequest;
import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtProperties jwtProperties;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username()))
            throw new IllegalArgumentException("Username ja esta em uso");
        if (userRepository.existsByEmail(request.email()))
            throw new IllegalArgumentException("Email ja esta em uso");

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role("USER")
                .build();

        UserEntity saved = userRepository.save(user);

        String accessToken = jwtService.generateToken(saved.getId(), saved.getUsername(), saved.getRole());
        String refreshToken = jwtService.generateRefreshToken(saved.getId(), saved.getUsername(), saved.getRole());

        refreshTokenService.createRefreshToken(saved);

        return new AuthResponse(accessToken, refreshToken, saved.getUsername(), saved.getEmail(), saved.getId());
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciais invalidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new BadCredentialsException("Credenciais invalidas");

        String accessToken = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername(), user.getRole());

        refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, user.getUsername(), user.getEmail(), user.getId());
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.validateRefreshToken(request.refreshToken());

        UserEntity user = refreshTokenEntity.getUser();

        String newAccessToken = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername(), user.getRole());

        refreshTokenService.revokeRefreshToken(request.refreshToken());

        refreshTokenService.createRefreshToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken, user.getUsername(), user.getEmail(), user.getId());
    }

    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            tokenBlacklistService.blacklistToken(accessToken);
        }

        if (refreshToken != null) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }
    }
}

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new BadCredentialsException("Credenciais inválidas");

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getId());
    }
}
