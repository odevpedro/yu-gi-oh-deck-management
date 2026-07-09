package com.odevpedro.yugiohcollections.creator.config;

import com.odevpedro.yugiohcollections.creator.domain.model.CardCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CardCreationException.class)
    public ResponseEntity<Map<String, Object>> handleCardCreation(CardCreationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody(
                400,
                "CARD_CREATION_ERROR",
                ex.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        Map<String, Object> body = errorBody(400, "VALIDATION_ERROR", "Invalid request");
        body.put("violations", violations);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody(
                400,
                "INVALID_ARGUMENT",
                ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody(
                500,
                "INTERNAL_ERROR",
                "Unexpected error while processing custom card request"
        ));
    }

    private Map<String, Object> errorBody(int statusCode, String error, String message) {
        return new java.util.LinkedHashMap<>(Map.of(
                "statusCode", statusCode,
                "error", error,
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
