package com.odevpedro.yugiohcollections.auth.application.service;

import com.odevpedro.yugiohcollections.auth.application.exception.TooManyLoginAttemptsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginRateLimitServiceTest {

    @Test
    void blocksAfterFiveFailuresAndResetsOnSuccess() {
        LoginRateLimitService service = new LoginRateLimitService();
        String ip = "127.0.0.1";

        for (int i = 0; i < 5; i++) {
            service.registerFailure(ip);
        }

        TooManyLoginAttemptsException ex = assertThrows(
                TooManyLoginAttemptsException.class,
                () -> service.assertAllowed(ip)
        );
        assertEquals("Muitas tentativas de login. Tente novamente mais tarde.", ex.getMessage());

        service.registerSuccess(ip);

        assertDoesNotThrow(() -> service.assertAllowed(ip));
    }
}
