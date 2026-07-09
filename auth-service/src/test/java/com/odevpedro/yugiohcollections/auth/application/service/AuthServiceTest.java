package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.RefreshTokenEntity;
import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.UserEntity;
import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.repository.UserJpaRepository;
import com.odevpedro.yugiohcollections.auth.application.dto.ForgotPasswordRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.LoginRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RefreshTokenRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RegisterRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.ResetPasswordRequest;
import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — cadastro, login e verificacao de email")
class AuthServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private JwtProperties jwtProperties;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(userRepository, passwordEncoder, jwtService, refreshTokenService, tokenBlacklistService, jwtProperties);
    }

    @Test
    void shouldRegisterUserWithEmailVerificationToken() {
        when(userRepository.existsByUsername("ash")).thenReturn(false);
        when(userRepository.existsByEmail("ash@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(jwtService.generateToken(any(), anyString(), anyString())).thenReturn("access");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity user = inv.getArgument(0);
            return UserEntity.builder()
                    .id(UUID.randomUUID())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .role(user.getRole())
                    .emailVerified(user.isEmailVerified())
                    .emailVerificationToken(user.getEmailVerificationToken())
                    .emailVerificationExpiresAt(user.getEmailVerificationExpiresAt())
                    .build();
        });
        when(refreshTokenService.createRefreshToken(any(UserEntity.class)))
                .thenReturn(RefreshTokenEntity.builder().token("refresh").build());

        var response = service.register(new RegisterRequest("ash", "ash@example.com", "secret123"));

        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh");
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().isEmailVerified()).isFalse();
        assertThat(captor.getValue().getEmailVerificationToken()).isNotBlank();
        verify(refreshTokenService).createRefreshToken(any(UserEntity.class));
    }

    @Test
    void shouldRotatePersistedRefreshToken() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("ash")
                .email("ash@example.com")
                .password("hashed")
                .role("USER")
                .emailVerified(true)
                .build();
        RefreshTokenEntity currentRefreshToken = RefreshTokenEntity.builder()
                .token("current-refresh")
                .user(user)
                .build();
        RefreshTokenEntity nextRefreshToken = RefreshTokenEntity.builder()
                .token("next-refresh")
                .user(user)
                .build();

        when(refreshTokenService.validateRefreshToken("current-refresh")).thenReturn(currentRefreshToken);
        when(jwtService.generateToken(user.getId(), user.getUsername(), user.getRole())).thenReturn("new-access");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(nextRefreshToken);

        var response = service.refreshToken(new RefreshTokenRequest("current-refresh"));

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("next-refresh");
        verify(refreshTokenService).revokeRefreshToken("current-refresh");
    }

    @Test
    void shouldRejectLoginWhenEmailIsNotVerified() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("ash")
                .email("ash@example.com")
                .password("hashed")
                .role("USER")
                .emailVerified(false)
                .build();

        when(userRepository.findByUsername("ash")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);

        assertThatThrownBy(() -> service.login(new LoginRequest("ash", "secret123")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email nao verificado");

        verifyNoInteractions(refreshTokenService);
    }

    @Test
    void shouldVerifyEmailByToken() {
        String token = "verification-token";
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("ash")
                .email("ash@example.com")
                .password("hashed")
                .role("USER")
                .emailVerified(false)
                .emailVerificationToken(token)
                .build();

        when(userRepository.findByEmailVerificationToken(token)).thenReturn(Optional.of(user));

        service.verifyEmail(token);

        assertThat(user.isEmailVerified()).isTrue();
        assertThat(user.getEmailVerificationToken()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void shouldRequestPasswordResetAndPersistToken() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("ash")
                .email("ash@example.com")
                .password("hashed")
                .role("USER")
                .emailVerified(true)
                .build();

        when(userRepository.findByEmail("ash@example.com")).thenReturn(Optional.of(user));

        service.requestPasswordReset("ash@example.com");

        assertThat(user.getPasswordResetToken()).isNotBlank();
        assertThat(user.getPasswordResetExpiresAt()).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void shouldResetPasswordAndRevokeTokens() {
        String token = "reset-token";
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("ash")
                .email("ash@example.com")
                .password("old-hash")
                .role("USER")
                .emailVerified(true)
                .passwordResetToken(token)
                .build();

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-secret")).thenReturn("new-hash");

        service.resetPassword(token, "new-secret");

        assertThat(user.getPassword()).isEqualTo("new-hash");
        assertThat(user.getPasswordResetToken()).isNull();
        assertThat(user.getPasswordResetExpiresAt()).isNull();
        verify(refreshTokenService).revokeAllUserTokens(user.getId());
        verify(userRepository).save(user);
    }
}
