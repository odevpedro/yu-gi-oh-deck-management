package com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository;

import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.MonsterCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonsterCardJpaRepository extends JpaRepository<MonsterCardEntity, Long> {
    List<MonsterCardEntity> findAllByOwnerId(String ownerId);
}