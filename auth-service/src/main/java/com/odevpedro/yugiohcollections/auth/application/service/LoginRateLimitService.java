package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.auth.application.exception.TooManyLoginAttemptsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimitService {

    private static final int MAX_FAILURES = 5;
    private static final Duration FAILURE_WINDOW = Duration.ofMinutes(1);
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private final ConcurrentHashMap<String, AttemptState> states = new ConcurrentHashMap<>();

    public void assertAllowed(String clientIp) {
        AttemptState state = states.get(clientIp);
        if (state == null) {
            return;
        }

        synchronized (state) {
            Instant now = Instant.now();
            pruneExpiredFailures(state, now);

            if (state.blockedUntil != null) {
                if (now.isBefore(state.blockedUntil)) {
                    long retryAfter = Math.max(1L, Duration.between(now, state.blockedUntil).toSeconds());
                    throw new TooManyLoginAttemptsException(
                            "Muitas tentativas de login. Tente novamente mais tarde.",
                            retryAfter
                    );
                }
                state.blockedUntil = null;
            }
        }
    }

    public void registerFailure(String clientIp) {
        AttemptState state = states.computeIfAbsent(clientIp, ignored -> new AttemptState());
        synchronized (state) {
            Instant now = Instant.now();
            pruneExpiredFailures(state, now);

            state.failures.addLast(now);
            if (state.failures.size() >= MAX_FAILURES) {
                state.blockedUntil = now.plus(BLOCK_DURATION);
                state.failures.clear();
            }
        }
    }

    public void registerSuccess(String clientIp) {
        states.remove(clientIp);
    }

    private void pruneExpiredFailures(AttemptState state, Instant now) {
        Instant cutoff = now.minus(FAILURE_WINDOW);
        while (!state.failures.isEmpty() && Objects.requireNonNull(state.failures.peekFirst()).isBefore(cutoff)) {
            state.failures.removeFirst();
        }
    }

    private static final class AttemptState {
        private final Deque<Instant> failures = new ArrayDeque<>();
        private Instant blockedUntil;
    }
}
