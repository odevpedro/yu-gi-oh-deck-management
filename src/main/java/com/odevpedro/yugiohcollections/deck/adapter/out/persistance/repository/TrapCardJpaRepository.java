package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.repository;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity.TrapCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrapCardJpaRepository extends JpaRepository<TrapCardEntity, Long> {
}