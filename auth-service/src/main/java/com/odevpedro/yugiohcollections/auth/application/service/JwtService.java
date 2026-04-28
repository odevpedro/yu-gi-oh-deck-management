package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UUID userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UUID userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId.toString())
                .claim("role", role)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpirationMs()))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).get("userId", String.class));
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getClaims(token);
        return "refresh".equals(claims.get("type", String.class));
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String getSecret() {
        return jwtProperties.getSecret();
    }
}