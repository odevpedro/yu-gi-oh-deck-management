package com.odevpedro.yugiohcollections.auth.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "blacklisted_at", nullable = false)
    private LocalDateTime blacklistedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}