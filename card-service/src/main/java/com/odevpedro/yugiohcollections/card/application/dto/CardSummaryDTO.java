package com.odevpedro.yugiohcollections.card.application.dto;

import com.odevpedro.yugiohcollections.card.domain.model.Card;
import lombok.Builder;

@Builder
public record CardSummaryDTO(Long cardId, String name, String type, String imageUrl, String description) {

    public static CardSummaryDTO from(Card card) {
        return new CardSummaryDTO(
                card.getId(),
                card.getName(),
                card.getType().name(),
                card.getImageUrl(),
                card.getDescription()
        );
    }
}