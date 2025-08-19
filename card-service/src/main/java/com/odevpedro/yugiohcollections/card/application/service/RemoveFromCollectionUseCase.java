package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.domain.model.ports.CollectionPersistencePort;
import org.springframework.stereotype.Service;

@Service
public class RemoveFromCollectionUseCase {
    private final CollectionPersistencePort port;

    public RemoveFromCollectionUseCase(CollectionPersistencePort port) {
        this.port = port;
    }

    public boolean remove(String userId, Long entryId) {
        return port.remove(userId, entryId);
    }
}
