package com.odevpedro.yugiohcollections.card.application.dto;

import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.MonsterCard;
import lombok.Builder;

@Builder
public record CardSummaryDTO(Long cardId, String name, String type, String imageUrl, String description,
                             Integer atk, Integer def, Integer level) {

    public static CardSummaryDTO from(Card card) {
        Integer atk = null;
        Integer def = null;
        Integer level = null;

        if (card instanceof MonsterCard monster) {
            atk = monster.getAttack();
            def = monster.getDefense();
            level = monster.getLevel();
        }

        return new CardSummaryDTO(
                card.getId(),
                card.getName(),
                card.getType().name(),
                card.getImageUrl(),
                card.getDescription(),
                atk,
                def,
                level
        );
    }
}
