package com.odevpedro.yugiohcollections.card.application.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(5_000)
                .expireAfterWrite(Duration.ofMinutes(15))
                .recordStats();
        CaffeineCacheManager mgr = new CaffeineCacheManager(
                "cardsByName", "cardsByFuzzy", "cardsByType", "cardsByTypeRace"
        );
        mgr.setCaffeine(caffeine);
        return mgr;
    }
}
