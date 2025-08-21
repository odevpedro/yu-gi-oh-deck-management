package com.odevpedro.yugiohcollections.deck.application.mapper;

import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckCardEntryEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckEntity;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.model.DeckZone;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeckMapper {

    public DeckEntity toEntity(Deck deck) {
        DeckEntity entity = new DeckEntity();
        entity.setId(deck.getId());
        entity.setName(deck.getName());
        entity.setOwnerId(deck.getOwnerId());

        // main
        if (deck.getMainDeck() != null) {
            for (Long cardId : deck.getMainDeck()) {
                DeckCardEntryEntity e = new DeckCardEntryEntity();
                e.setCardId(cardId);
                e.setQuantity(1);              // por padrão 1 por ocorrência na lista
                e.setZone(DeckZone.MAIN);
                entity.addEntry(e);
            }
        }
        // extra
        if (deck.getExtraDeck() != null) {
            for (Long cardId : deck.getExtraDeck()) {
                DeckCardEntryEntity e = new DeckCardEntryEntity();
                e.setCardId(cardId);
                e.setQuantity(1);
                e.setZone(DeckZone.EXTRA);
                entity.addEntry(e);
            }
        }
        // side
        if (deck.getSideDeck() != null) {
            for (Long cardId : deck.getSideDeck()) {
                DeckCardEntryEntity e = new DeckCardEntryEntity();
                e.setCardId(cardId);
                e.setQuantity(1);
                e.setZone(DeckZone.SIDE);
                entity.addEntry(e);
            }
        }
        return entity;
    }

    public Deck toDomain(DeckEntity entity) {
        List<Long> main  = new ArrayList<>();
        List<Long> extra = new ArrayList<>();
        List<Long> side  = new ArrayList<>();

        if (entity.getEntries() != null) {
            for (DeckCardEntryEntity e : entity.getEntries()) {
                List<Long> target;
                if (e.getZone() == DeckZone.EXTRA) target = extra;
                else if (e.getZone() == DeckZone.SIDE) target = side;
                else target = main; // default MAIN

                // replica o cardId conforme a quantidade
                int q = Math.max(e.getQuantity(), 1);
                for (int i = 0; i < q; i++) target.add(e.getCardId());
            }
        }

        return new Deck(
                entity.getId(),
                entity.getName(),
                entity.getOwnerId(),
                main, extra, side
        );
    }
}