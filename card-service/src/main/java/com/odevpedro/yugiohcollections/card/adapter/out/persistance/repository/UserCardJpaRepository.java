package com.odevpedro.yugiohcollections.card.adapter.out.persistance.repository;


import com.odevpedro.yugiohcollections.card.adapter.out.persistance.entity.UserCardEntity;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCardJpaRepository extends JpaRepository<UserCardEntity, Long> {

    List<UserCardEntity> findAllByUserIdOrderByIdAsc(String userId);

    Optional<UserCardEntity> findByUserIdAndCardTypeAndCardId(String userId, CardType cardType, Long cardId);

    boolean existsByUserIdAndCardTypeAndCardId(String userId, CardType cardType, Long cardId);

    int deleteByUserIdAndId(String userId, Long id);
}

