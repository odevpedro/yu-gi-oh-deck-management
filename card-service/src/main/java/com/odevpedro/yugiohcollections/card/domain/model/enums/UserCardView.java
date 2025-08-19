package com.odevpedro.yugiohcollections.card.domain.model.enums;

public record UserCardView(
        Long id,
        String userId,
        CardType cardType,
        Long cardId,
        Integer quantity,
        String notes
) {}
