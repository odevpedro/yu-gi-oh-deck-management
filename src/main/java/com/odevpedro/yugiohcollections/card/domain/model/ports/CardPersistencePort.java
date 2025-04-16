package com.odevpedro.yugiohcollections.card.domain.model.ports;

import com.odevpedro.yugiohcollections.card.domain.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardPersistencePort {
    Card save(Card card);

    List<Card> findAllByOwnerId(String ownerId);

    Optional<Card> updateCard(Long id, Card updatedCard);

    Optional<Boolean> deleteByIdAndOwner(Long id, String ownerId);
}