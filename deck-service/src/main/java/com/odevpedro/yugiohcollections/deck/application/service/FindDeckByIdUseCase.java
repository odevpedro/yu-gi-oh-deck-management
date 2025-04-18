package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindDeckByIdUseCase {

    private final DeckRepositoryPort repository;

    public FindDeckByIdUseCase(DeckRepositoryPort repository) {
        this.repository = repository;
    }

    public Optional<Deck> execute(Long deckId, String ownerId) {
        return repository.findByIdAndOwnerId(deckId, ownerId);
    }
}
