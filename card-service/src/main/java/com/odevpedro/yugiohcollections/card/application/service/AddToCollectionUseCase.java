package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.UserCardView;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CollectionPersistencePort;
import org.springframework.stereotype.Service;

@Service
public class AddToCollectionUseCase {
    private final CollectionPersistencePort port;

    public AddToCollectionUseCase(CollectionPersistencePort port) {
        this.port = port;
    }
    public UserCardView add(String userId, CardType type, Long cardId, int quantity, String notes) {
        return port.addOrIncrement(userId, type, cardId, quantity, notes);
    }
}
