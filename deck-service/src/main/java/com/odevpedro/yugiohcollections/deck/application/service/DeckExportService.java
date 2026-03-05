package com.odevpedro.yugiohcollections.deck.application.service;


public interface DeckExportService {
    String exportAsYdk(Long deckId, String ownerId);
}
