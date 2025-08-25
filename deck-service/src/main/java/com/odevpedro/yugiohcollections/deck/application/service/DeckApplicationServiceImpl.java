package com.odevpedro.yugiohcollections.deck.application.service;

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

        // ⚠️ Por padrão adicionamos ao mainDeck — futuramente pode haver lógica para decidir onde vai a carta
        for (int i = 0; i < quantity; i++) {
            deck.addToMain(cardId); // ou .addToExtra/.addToSide futuramente
        }

        // 🧠 Aqui você pode chamar um DeckRules futuramente:
        // deckRules.validate(deck);

        return deckRepository.save(deck);
    }
}