package com.odevpedro.yugiohcollections.card.application.service;

import com.odevpedro.yugiohcollections.card.domain.model.ports.CardPersistencePort;
import org.springframework.stereotype.Service;

@Service
public class DeleteCardUseCase {

    private final CardPersistencePort persistencePort;

    public DeleteCardUseCase(CardPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    public boolean delete(Long id, String ownerId) {
        return persistencePort.deleteByIdAndOwner(id, ownerId).orElse(false);
    }
}