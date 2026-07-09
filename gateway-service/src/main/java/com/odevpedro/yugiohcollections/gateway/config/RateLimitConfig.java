package com.odevpedro.yugiohcollections.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public GlobalFilter gatewayRateLimitFilter(
            @Value("${gateway.rate-limit.requests-per-window:120}") int requestsPerWindow,
            @Value("${gateway.rate-limit.window:PT1M}") Duration window) {
        return new SimpleRateLimitFilter(requestsPerWindow, window);
    }
}
