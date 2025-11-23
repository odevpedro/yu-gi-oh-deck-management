package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardFeignClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.DeckRepositoryAdapter;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeckApplicationServiceImpl implements DeckApplicationService {

    private final DeckRepositoryAdapter deckRepository;
    private final CardFeignClient cardFeignClient;

    @Override
    public Deck createDeck(String ownerId, String name) {
        Deck deck = Deck.of(ownerId, name); // ✅ método correto
        return deckRepository.save(deck);
    }

    @Override
    public List<Deck> listDecks(String ownerId) {
        return deckRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Deck getDeck(String ownerId, Long deckId) {
        return deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck não encontrado para este usuário"));
    }

    @Override
    public Deck addCard(String ownerId, Long deckId, Long cardId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser > 0");
        }

        Deck deck = deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck não encontrado para este usuário"));

        for (int i = 0; i < quantity; i++) {
            deck.addToMain(cardId);
        }


        return deckRepository.save(deck);
    }

    @Override
    public DeckView getDeckWithCards(String ownerId, Long deckId) throws Exception {
        Deck deck = getDeck(ownerId, deckId);
        List<Long> ids = deck.allCardIds();
        List<CardSummaryDTO> enriched = cardFeignClient.findCardsByIds(ids);
        return DeckView.from(deck, enriched);
    }
}