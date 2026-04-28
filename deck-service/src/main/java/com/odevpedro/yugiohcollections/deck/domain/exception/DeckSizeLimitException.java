package com.odevpedro.yugiohcollections.deck.domain.exception;

public class DeckSizeLimitException extends RuntimeException {

    private final String zone;
    private final int currentSize;
    private final int minSize;
    private final int maxSize;

    public DeckSizeLimitException(String zone, int currentSize, int minSize, int maxSize) {
        super(String.format("Zone %s: tamanho deve estar entre %d e %d (atual: %d)", zone, minSize, maxSize, currentSize));
        this.zone = zone;
        this.currentSize = currentSize;
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    public String getZone() {
        return zone;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }
}