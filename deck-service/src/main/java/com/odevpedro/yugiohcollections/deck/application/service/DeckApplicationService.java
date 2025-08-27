package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.cardcatalog.CardCatalogClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckCardEntryEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.repository.DeckJpaRepository;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public interface DeckApplicationService {
        Deck createDeck(String ownerId, String name);
        List<Deck> listDecks(String ownerId);
        Deck getDeck(String ownerId, Long deckId) throws Exception;
        Deck addCard(String ownerId, Long deckId, Long cardId, int quantity) throws Exception;
        DeckView getDeckWithCards(String ownerId, Long deckId) throws Exception;
}