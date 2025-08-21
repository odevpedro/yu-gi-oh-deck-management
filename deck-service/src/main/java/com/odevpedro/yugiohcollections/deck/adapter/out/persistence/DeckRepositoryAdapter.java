package com.odevpedro.yugiohcollections.deck.adapter.out.persistence;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.repository.DeckJpaRepository;
import com.odevpedro.yugiohcollections.deck.application.mapper.DeckMapper;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DeckRepositoryAdapter implements DeckRepositoryPort {

    private final DeckJpaRepository deckJpaRepository;
    private final DeckMapper deckMapper;

    public DeckRepositoryAdapter(DeckJpaRepository deckJpaRepository, DeckMapper deckMapper) {
        this.deckJpaRepository = deckJpaRepository;
        this.deckMapper = deckMapper;
    }

    @Override
    public Deck save(Deck deck) {
        var entity = deckMapper.toEntity(deck);
        var saved = deckJpaRepository.save(entity);
        return deckMapper.toDomain(saved);
    }

    @Override
    public Optional<Deck> findByIdAndOwnerId(Long id, String ownerId) {
        System.out.println(ownerId);
        return deckJpaRepository.findByOwnerIdAndId(ownerId, id)
                .map(deckMapper::toDomain);

    }


    @Override
    public List<Deck> findAllByOwnerId(String ownerId) {
        return deckJpaRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(deckMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndOwnerId(Long id, String ownerId) {
        deckJpaRepository.deleteByIdAndOwnerId(id, ownerId);
    }
}