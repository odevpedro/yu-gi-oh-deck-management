package com.odevpedro.yugiohcollections.card.domain.model.enums;

public enum CardType {
    MONSTER,
    SPELL,
    TRAP;

    public static CardType fromYgoProType(String rawType) {
        if (rawType == null || rawType.isBlank()) {
            throw new IllegalArgumentException("Tipo de carta não pode ser nulo ou vazio");
        }

        String normalized = rawType.trim().toUpperCase();

        if (normalized.contains("SPELL")) return SPELL;
        if (normalized.contains("TRAP")) return TRAP;
        return MONSTER;
    }
}