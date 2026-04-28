
package com.odevpedro.yugiohcollections.auth.config;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import com.odevpedro.yugiohcollections.shared.security.JwtAuthFilter;
import com.odevpedro.yugiohcollections.shared.security.TokenValidationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtProperties jwtProperties,
                                       @Value("${jwt.skip-blacklist-check:true}") boolean skipBlacklistCheck,
                                       TokenValidationClient tokenValidationClient) {
        return new JwtAuthFilter(jwtProperties, skipBlacklistCheck, tokenValidationClient);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}