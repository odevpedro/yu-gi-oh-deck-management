package com.odevpedro.yugiohcollections.deck.adapter.out.persistance.repository;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistance.entity.MonsterCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonsterCardJpaRepository extends JpaRepository<MonsterCardEntity, Long> {
}