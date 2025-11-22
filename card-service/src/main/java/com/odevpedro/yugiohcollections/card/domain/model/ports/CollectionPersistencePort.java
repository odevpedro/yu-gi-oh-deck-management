package com.odevpedro.yugiohcollections.card.domain.model.ports;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.UserCardView;

import java.util.Optional;

public interface CollectionPersistencePort {
    UserCardView addOrIncrement(String userId, CardType cardType, Long cardId, int quantity, String notes);
    Optional<UserCardView> updateQuantity(String userId, Long entryId, int quantity, String notes);
    boolean remove(String userId, Long entryId);
}

