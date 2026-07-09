package com.odevpedro.yugiohcollections.shared.config;

import com.odevpedro.yugiohcollections.shared.context.CorrelationIdContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class CorrelationIdConfiguration {

    public static final String HEADER_NAME = "X-Correlation-Id";
    public static final String ATTRIBUTE_NAME = "correlationId";

    @Bean
    public OncePerRequestFilter correlationIdFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String correlationId = request.getHeader(HEADER_NAME);
                if (correlationId == null || correlationId.isBlank()) {
                    correlationId = UUID.randomUUID().toString();
                }

                CorrelationIdContext.set(correlationId);
                request.setAttribute(ATTRIBUTE_NAME, correlationId);
                response.setHeader(HEADER_NAME, correlationId);

                try {
                    filterChain.doFilter(request, response);
                } finally {
                    CorrelationIdContext.clear();
                }
            }
        };
    }

}
