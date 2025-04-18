package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateDeckUseCase {

    private final DeckRepositoryPort repository;

    public CreateDeckUseCase(DeckRepositoryPort repository) {
        this.repository = repository;
    }

    public Deck execute(Deck deck) {
        return repository.save(deck);
    }
}