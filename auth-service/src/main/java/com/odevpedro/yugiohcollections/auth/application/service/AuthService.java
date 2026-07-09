package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.UserEntity;
import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.repository.UserJpaRepository;
import com.odevpedro.yugiohcollections.auth.application.dto.ForgotPasswordRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.AuthResponse;
import com.odevpedro.yugiohcollections.auth.application.dto.LoginRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RefreshTokenRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RegisterRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.ResetPasswordRequest;
import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
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
                .emailVerified(false)
                .emailVerificationToken(generateVerificationToken())
                .emailVerificationExpiresAt(LocalDateTime.now().plusDays(1))
                .build();

        UserEntity saved = userRepository.save(user);
        log.info("Verification link for {}: /auth/verify?token={}", saved.getEmail(), saved.getEmailVerificationToken());

        String accessToken = jwtService.generateToken(saved.getId(), saved.getUsername(), saved.getRole());
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(saved);

        return new AuthResponse(accessToken, refreshToken.getToken(), saved.getUsername(), saved.getEmail(), saved.getId());
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciais invalidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new BadCredentialsException("Credenciais invalidas");
        if (!user.isEmailVerified())
            throw new IllegalStateException("Email nao verificado");

        String accessToken = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getUsername(), user.getEmail(), user.getId());
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.validateRefreshToken(request.refreshToken());

        UserEntity user = refreshTokenEntity.getUser();

        String newAccessToken = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());

        refreshTokenService.revokeRefreshToken(request.refreshToken());

        RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken.getToken(), user.getUsername(), user.getEmail(), user.getId());
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

    public void verifyEmail(String token) {
        UserEntity user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de verificacao invalido"));

        if (user.getEmailVerificationExpiresAt() != null && user.getEmailVerificationExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token de verificacao expirado");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiresAt(null);
        userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email nao encontrado"));

        user.setPasswordResetToken(generateVerificationToken());
        user.setPasswordResetExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        log.info("Password reset link for {}: /auth/reset-password?token={}", user.getEmail(), user.getPasswordResetToken());
    }

    public void resetPassword(String token, String newPassword) {
        UserEntity user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de reset invalido"));

        if (user.getPasswordResetExpiresAt() != null && user.getPasswordResetExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token de reset expirado");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpiresAt(null);
        userRepository.save(user);
        refreshTokenService.revokeAllUserTokens(user.getId());
    }

    private String generateVerificationToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
