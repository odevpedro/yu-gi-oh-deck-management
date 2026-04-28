package com.odevpedro.yugiohcollections.deck.config;

import com.odevpedro.yugiohcollections.deck.domain.exception.DeckValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeckValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDeckValidation(DeckValidationException ex) {
        log.warn("Deck validation failed: {}", ex.getViolations());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "statusCode", 400,
                "error", "DECK_VALIDATION_FAILED",
                "message", "Regras do deck violadas",
                "violations", ex.getViolations(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "statusCode", 400,
                "error", "INVALID_ARGUMENT",
                "message", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "statusCode", 500,
                "error", "INTERNAL_ERROR",
                "message", "Erro interno do servidor",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}