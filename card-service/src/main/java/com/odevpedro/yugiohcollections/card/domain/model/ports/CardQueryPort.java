package com.odevpedro.yugiohcollections.card.domain.model.ports;

import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.enums.CardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardQueryPort {
    List<Card> findAllByIds(List<Long> ids);
    Page<Card> findAllByType(CardType type, Pageable pageable);

}
