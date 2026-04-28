package com.odevpedro.yugiohcollections.deck.domain.exception;

import java.util.List;

public class DeckValidationException extends RuntimeException {

    private final List<String> violations;

    public DeckValidationException(String message) {
        super(message);
        this.violations = List.of(message);
    }

    public DeckValidationException(List<String> violations) {
        super("Deck validation failed: " + String.join(", ", violations));
        this.violations = violations;
    }

    public List<String> getViolations() {
        return violations;
    }
}