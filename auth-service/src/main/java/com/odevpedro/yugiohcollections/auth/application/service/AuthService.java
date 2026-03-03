package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity.UserEntity;
import com.odevpedro.yugiohcollections.auth.adapter.out.persistence.repository.UserJpaRepository;
import com.odevpedro.yugiohcollections.auth.application.dto.AuthResponse;
import com.odevpedro.yugiohcollections.auth.application.dto.LoginRequest;
import com.odevpedro.yugiohcollections.auth.application.dto.RegisterRequest;
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

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username()))
            throw new IllegalArgumentException("Username já está em uso");
        if (userRepository.existsByEmail(request.email()))
            throw new IllegalArgumentException("Email já está em uso");

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role("USER")
                .build();

        UserEntity saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId(), saved.getUsername(), saved.getRole());

        return new AuthResponse(token, saved.getUsername(), saved.getEmail(), saved.getId());
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
