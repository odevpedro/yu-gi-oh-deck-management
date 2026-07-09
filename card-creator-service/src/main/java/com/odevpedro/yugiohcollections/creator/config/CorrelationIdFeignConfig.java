package com.odevpedro.yugiohcollections.creator.config;

import com.odevpedro.yugiohcollections.shared.context.CorrelationIdContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorrelationIdFeignConfig {

    @Bean
    public RequestInterceptor correlationIdRequestInterceptor() {
        return template -> {
            String correlationId = CorrelationIdContext.get();
            if (correlationId != null && !correlationId.isBlank()) {
                template.header("X-Correlation-Id", correlationId);
            }
        };
    }
}
