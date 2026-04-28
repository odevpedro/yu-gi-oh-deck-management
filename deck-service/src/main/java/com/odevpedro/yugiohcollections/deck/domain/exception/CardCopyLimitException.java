package com.odevpedro.yugiohcollections.deck.domain.exception;

public class CardCopyLimitException extends RuntimeException {

    private final Long cardId;
    private final int currentCopies;
    private final int maxCopies;

    public CardCopyLimitException(Long cardId, int currentCopies, int maxCopies) {
        super(String.format("Carta ID %d: limite de %d copias excedido (atual: %d)", cardId, maxCopies, currentCopies));
        this.cardId = cardId;
        this.currentCopies = currentCopies;
        this.maxCopies = maxCopies;
    }

    public Long getCardId() {
        return cardId;
    }

    public int getCurrentCopies() {
        return currentCopies;
    }

    public int getMaxCopies() {
        return maxCopies;
    }
}