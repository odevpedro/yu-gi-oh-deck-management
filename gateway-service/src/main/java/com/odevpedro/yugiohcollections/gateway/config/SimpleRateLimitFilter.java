package com.odevpedro.yugiohcollections.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleRateLimitFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(SimpleRateLimitFilter.class);

    private final int requestsPerWindow;
    private final Duration window;
    private final Map<String, WindowState> windows = new ConcurrentHashMap<>();

    public SimpleRateLimitFilter(int requestsPerWindow, Duration window) {
        this.requestsPerWindow = requestsPerWindow;
        this.window = window;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        String clientKey = resolveClientKey(exchange);
        WindowState state = windows.computeIfAbsent(clientKey, key -> new WindowState(Instant.now(), 0));

        synchronized (state) {
            Instant now = Instant.now();
            if (now.isAfter(state.windowStart.plus(window))) {
                state.windowStart = now;
                state.count = 0;
            }

            state.count++;
            if (state.count > requestsPerWindow) {
                long retryAfter = Math.max(1, Duration.between(now, state.windowStart.plus(window)).toSeconds());
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().add(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter));
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private String resolveClientKey(ServerWebExchange exchange) {
        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        InetSocketAddress address = exchange.getRequest().getRemoteAddress();
        if (address != null && address.getAddress() != null) {
            return address.getAddress().getHostAddress();
        }

        return "unknown";
    }

    private static final class WindowState {
        private Instant windowStart;
        private int count;

        private WindowState(Instant windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
