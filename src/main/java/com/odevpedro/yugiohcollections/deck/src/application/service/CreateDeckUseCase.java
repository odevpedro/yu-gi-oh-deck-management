package com.odevpedro.yugiohcollections.deck.src.application.service;

import com.odevpedro.yugiohcollections.deck.src.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.src.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateDeckUseCase {

    private final DeckRepositoryPort deckRepositoryPort;

    public CreateDeckUseCase(DeckRepositoryPort deckRepositoryPort) {
        this.deckRepositoryPort = deckRepositoryPort;
    }

    public Deck execute(Deck deck) {
        return deckRepositoryPort.save(deck);
    }
}