package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;

import java.util.List;

public interface DeckApplicationService {
    Deck createDeck(String ownerId, String name);
    List<Deck> listDecks(String ownerId);
    Deck getDeck(String ownerId, Long deckId);
    Deck addCard(String ownerId, Long deckId, Long cardId, int quantity);
    Deck removeCard(String ownerId, Long deckId, Long cardId, String zone);
    DeckView getDeckWithCards(String ownerId, Long deckId);
    void deleteDeck(String ownerId, Long deckId);
}