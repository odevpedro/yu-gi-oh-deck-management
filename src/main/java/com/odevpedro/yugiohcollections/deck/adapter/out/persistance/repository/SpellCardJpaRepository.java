package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.repository;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity.SpellCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpellCardJpaRepository extends JpaRepository<SpellCardEntity, Long> {
    List<SpellCardEntity> findAllByOwnerId(String ownerId);
}