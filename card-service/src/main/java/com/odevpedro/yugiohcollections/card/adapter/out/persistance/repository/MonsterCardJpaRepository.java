package com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository;

import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.MonsterCardEntity;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterAttribute;
import com.odevpedro.yugiohcollections.card.domain.model.enums.MonsterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonsterCardJpaRepository extends JpaRepository<MonsterCardEntity, Long> {

    List<MonsterCardEntity> findAllByOwnerId(String ownerId);

    // ---- checagem/recuperação por chave natural mínima (idempotência) ----
    boolean existsByOwnerIdAndNameIgnoreCaseAndType(
            String ownerId,
            String name,
            CardType type
    );

    Optional<MonsterCardEntity> findByOwnerIdAndNameIgnoreCaseAndType(
            String ownerId,
            String name,
            CardType type
    );

    Optional<MonsterCardEntity> findByOwnerIdAndNameIgnoreCaseAndTypeAndMonsterType(
            String ownerId,
            String name,
            CardType type,
            MonsterType monsterType
    );

    Optional<MonsterCardEntity> findByOwnerIdAndNameIgnoreCaseAndTypeAndLevelAndAttributeAndMonsterType(
            String ownerId,
            String name,
            CardType type,
            Integer level,
            MonsterAttribute attribute,
            MonsterType monsterType
    );

    List<MonsterCardEntity> findAllByType(CardType type);
}
