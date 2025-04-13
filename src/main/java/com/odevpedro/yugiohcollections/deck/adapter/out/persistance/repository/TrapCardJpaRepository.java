package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.repository;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity.TrapCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrapCardJpaRepository extends JpaRepository<TrapCardEntity, Long> {
    List<TrapCardEntity> findAllByOwnerId(String ownerId);
}