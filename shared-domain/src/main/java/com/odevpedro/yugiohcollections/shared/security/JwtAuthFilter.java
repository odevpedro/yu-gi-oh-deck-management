package com.odevpedro.yugiohcollections.shared.security;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final TokenValidationClient tokenValidationClient;
    private final boolean skipBlacklistCheck;

    public JwtAuthFilter(JwtProperties jwtProperties,
                         @Value("${jwt.skip-blacklist-check:false}") boolean skipBlacklistCheck,
                         TokenValidationClient tokenValidationClient) {
        this.jwtProperties = jwtProperties;
        this.skipBlacklistCheck = skipBlacklistCheck;
        this.tokenValidationClient = tokenValidationClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7).trim();

            if (!skipBlacklistCheck) {
                Map<String, Object> validationResult = tokenValidationClient.validateToken(authHeader);
                Boolean isValid = (Boolean) validationResult.get("valid");
                if (isValid != null && !isValid) {
                    log.warn("Token blacklisted or invalid: {}", validationResult.get("reason"));
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.get("userId", String.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            auth.setDetails(userId);

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            log.error("JwtAuthFilter error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/register") ||
               path.startsWith("/auth/login") ||
               path.startsWith("/auth/verify") ||
               path.startsWith("/auth/refresh") ||
               path.startsWith("/internal/") ||
               path.startsWith("/actuator/");
    }
}
