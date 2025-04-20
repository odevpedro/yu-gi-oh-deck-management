package com.odevpedro.yugiohcollections.card.domain.model.ports;

import com.odevpedro.yugiohcollections.card.domain.model.Card;

import java.util.List;

public interface CardQueryPort {
    List<Card> findAllByIds(List<Long> ids);

}
