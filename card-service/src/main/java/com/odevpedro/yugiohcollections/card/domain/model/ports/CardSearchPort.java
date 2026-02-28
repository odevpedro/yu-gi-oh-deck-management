package com.odevpedro.yugiohcollections.card.domain.model.ports;

import com.odevpedro.yugiohcollections.card.domain.model.Card;

import java.util.List;

public interface CardSearchPort {
    List<Card> searchByName(String name);
    List<Card> searchByFuzzyName(String fname);
    List<Card> searchByType(String type);
    List<Card> searchByTypeAndRace(String type, String race);
}