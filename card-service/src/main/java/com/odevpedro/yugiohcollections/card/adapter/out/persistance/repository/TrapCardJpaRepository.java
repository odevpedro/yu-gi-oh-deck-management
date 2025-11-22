package com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository;

import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.TrapCardEntity;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import com.odevpedro.yugiohcollections.card.domain.model.enums.TrapType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrapCardJpaRepository extends JpaRepository<TrapCardEntity, Long> {

    List<TrapCardEntity> findAllByOwnerId(String ownerId);

    boolean existsByOwnerIdAndNameIgnoreCaseAndTypeAndTrapType(
            String ownerId,
            String name,
            CardType type,
            TrapType trapType
    );

    Optional<TrapCardEntity> findByOwnerIdAndNameIgnoreCaseAndTypeAndTrapType(
            String ownerId,
            String name,
            CardType type,
            TrapType trapType
    );

    List<TrapCardEntity> findAllByType(CardType type);
}
