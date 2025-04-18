package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListDecksByOwnerUseCase {

    private final DeckRepositoryPort repository;

    public ListDecksByOwnerUseCase(DeckRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Deck> execute(String ownerId) {
        return repository.findAllByOwnerId(ownerId);
    }
}