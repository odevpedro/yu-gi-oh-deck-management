package com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository;

import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.SpellCardEntity;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.SpellType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpellCardJpaRepository extends JpaRepository<SpellCardEntity, Long> {
    // já existia
    List<SpellCardEntity> findAllByOwnerId(String ownerId);

    boolean existsByOwnerIdAndNameIgnoreCaseAndTypeAndSpellType(
            String ownerId,
            String name,
            CardType type,
            SpellType spellType
    );

    Optional<SpellCardEntity> findByOwnerIdAndNameIgnoreCaseAndTypeAndSpellType(
            String ownerId,
            String name,
            CardType type,
            SpellType spellType
    );

    List<SpellCardEntity> findAllByType(CardType type);
}