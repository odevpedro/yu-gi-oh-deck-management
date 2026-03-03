
package com.odevpedro.yugiohcollections.auth.config;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import com.odevpedro.yugiohcollections.shared.security.JwtAuthFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtProperties jwtProperties) {
        return new JwtAuthFilter(jwtProperties);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}