package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.domain.model.enums.UserCardView;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CollectionPersistencePort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateEntryUseCase {
    private final CollectionPersistencePort port;

    public UpdateEntryUseCase(CollectionPersistencePort port) {
        this.port = port;
    }

    public Optional<UserCardView> update(String userId, Long entryId, int quantity, String notes) {
        return port.updateQuantity(userId, entryId, quantity, notes);
    }
}
