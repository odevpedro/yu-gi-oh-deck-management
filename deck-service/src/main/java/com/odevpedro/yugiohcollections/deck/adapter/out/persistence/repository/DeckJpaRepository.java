package com.odevpedro.yugiohcollections.deck.adapter.out.persistence.repository;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeckJpaRepository extends JpaRepository<DeckEntity, Long> {
    List<DeckEntity> findAllByOwnerId(String ownerId);
    void deleteByIdAndOwnerId(Long id, String ownerId);

    Optional<DeckEntity> findByOwnerIdAndId(String ownerId, Long id);

}