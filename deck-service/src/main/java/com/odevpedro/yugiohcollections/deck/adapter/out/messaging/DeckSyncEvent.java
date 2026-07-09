package com.odevpedro.yugiohcollections.deck.adapter.out.messaging;

import java.time.Instant;
import java.util.List;

public record DeckSyncEvent(
        Long deckId,
        String ownerId,
        String name,
        String action,
        List<Long> mainDeck,
        List<Long> extraDeck,
        List<Long> sideDeck,
        Instant occurredAt
) {}
