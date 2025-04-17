package com.odevpedro.yugiohcollections.deck.src.application.service;

import com.odevpedro.yugiohcollections.deck.src.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.src.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindDeckByIdUseCase {

    private final DeckRepositoryPort deckRepositoryPort;

    public FindDeckByIdUseCase(DeckRepositoryPort deckRepositoryPort) {
        this.deckRepositoryPort = deckRepositoryPort;
    }

    public Optional<Deck> execute(Long id, String ownerId) {
        return deckRepositoryPort.findByIdAndOwnerId(id, ownerId);
    }
}