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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeckApplicationService {

    private final CardCatalogClient catalog;   // cliente Feign para card-service
    private final DeckJpaRepository deckRepo;  // JPA do deck-service

    /** Cria um deck vazio apenas com nome e ownerId */
    @Transactional
    public Deck createDeck(String ownerId, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome do deck é obrigatório");
        }

        DeckEntity e = new DeckEntity();
        e.setOwnerId(ownerId);
        e.setName(name);
        e.setEntries(new ArrayList<>());

        DeckEntity saved = deckRepo.save(e);

        return Deck.builder()
                .id(saved.getId())
                .ownerId(saved.getOwnerId())
                .name(saved.getName())
                .cards(new ArrayList<>())
                .totalCards(0)
                .notes(null)
                .build();
    }

    /** Adiciona (ou incrementa) uma carta no deck */
    @CircuitBreaker(name = "cardCatalog", fallbackMethod = "fallbackAdd")
    @Retry(name = "cardCatalog")
    @Transactional
    public DeckView addCard(String ownerId, Long deckId, Long cardId, int qty)
            throws ChangeSetPersister.NotFoundException {

        // 1) valida carta no catálogo externo (card-service)
        CardSummaryDTO card = catalog.getById(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Card inexistente: " + cardId);
        }

        // 2) busca deck do usuário
        DeckEntity deck = deckRepo.findByOwnerIdAndId(ownerId, deckId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // 3) incrementa se já existe; senão cria entry
        DeckCardEntryEntity existing = deck.getEntries().stream()
                .filter(e -> e.getCardId().equals(cardId))
                .findFirst()
                .orElse(null);

        int delta = Math.max(qty, 1);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + delta);
        } else {
            DeckCardEntryEntity entry = new DeckCardEntryEntity();
            entry.setCardId(cardId);
            entry.setQuantity(delta);
            deck.addEntry(entry); // método da entidade que mantém a relação bidirecional
        }

        // 4) persiste e monta view com informação mínima resolvida da carta adicionada
        deckRepo.save(deck);

        Map<Long, CardSummaryDTO> info = Map.of(cardId, card);
        return DeckView.from(deck, info);
    }

    // fallback do circuit breaker
    @SuppressWarnings("unused")
    private DeckView fallbackAdd(String ownerId, Long deckId, Long cardId, int qty, Throwable t) {
        throw new IllegalStateException("Catálogo de cartas indisponível no momento. Tente novamente mais tarde.", t);
    }
}