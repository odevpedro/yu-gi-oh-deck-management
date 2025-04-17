package com.odevpedro.yugiohcollections.deck.src.domain.port;

import com.odevpedro.yugiohcollections.deck.src.domain.model.Deck;

import java.util.List;
import java.util.Optional;

public interface DeckRepositoryPort {

    Deck save(Deck deck);

    Optional<Deck> findByIdAndOwnerId(Long id, String ownerId);

    List<Deck> findAllByOwnerId(String ownerId);

    void deleteByIdAndOwnerId(Long id, String ownerId);
}