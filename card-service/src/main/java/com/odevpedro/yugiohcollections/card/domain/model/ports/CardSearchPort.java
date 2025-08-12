package com.odevpedro.yugiohcollections.card.domain.model.ports;

import com.odevpedro.yugiohcollections.card.domain.model.Card;

import java.util.List;

public interface CardSearchPort {
    List<Card> searchByName(String name);                 // match exato
    List<Card> searchByFuzzyName(String fname);           // fuzzy (fname)
    List<Card> searchByType(String type);                 // SPELL|TRAP|MONSTER
    List<Card> searchByTypeAndRace(String type, String race); // ex.: SPELL + Equip;
}