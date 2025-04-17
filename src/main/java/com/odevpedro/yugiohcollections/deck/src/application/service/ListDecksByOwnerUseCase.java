package com.odevpedro.yugiohcollections.deck.src.application.service;

import com.odevpedro.yugiohcollections.deck.src.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.src.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListDecksByOwnerUseCase {

    private final DeckRepositoryPort deckRepositoryPort;

    public ListDecksByOwnerUseCase(DeckRepositoryPort deckRepositoryPort) {
        this.deckRepositoryPort = deckRepositoryPort;
    }

    public List<Deck> execute(String ownerId) {
        return deckRepositoryPort.findAllByOwnerId(ownerId);
    }
}