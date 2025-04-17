package com.odevpedro.yugiohcollections.deck.src.application.service;

import com.odevpedro.yugiohcollections.deck.src.domain.port.DeckRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteDeckUseCase {

    private final DeckRepositoryPort deckRepositoryPort;

    public DeleteDeckUseCase(DeckRepositoryPort deckRepositoryPort) {
        this.deckRepositoryPort = deckRepositoryPort;
    }

    @Transactional
    public void execute(Long id, String ownerId) {
        deckRepositoryPort.deleteByIdAndOwnerId(id, ownerId);
    }
}