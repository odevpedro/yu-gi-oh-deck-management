package com.odevpedro.yugiohcollections.domain.model.ports;

import com.odevpedro.yugiohcollections.domain.model.Card;

import java.util.List;

public interface CardSearchPort {
    List<Card> searchByName(String name);
}
