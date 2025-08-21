package com.odevpedro.yugiohcollections.deck.adapter.out.persistence.repository;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckCardEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeckEntryJpaRepository extends JpaRepository<DeckCardEntryEntity, Long> {
    List<DeckCardEntryEntity> findAllByDeck_Id(Long deckId);
    Optional<DeckCardEntryEntity> findByDeck_IdAndCardId(Long deckId, Long cardId);
    void deleteByDeck_Id(Long deckId);
}
