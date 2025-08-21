package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.cardcatalog.CardCatalogClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckCardEntryEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.entity.DeckEntity;
import com.odevpedro.yugiohcollections.deck.adapter.out.persistence.repository.DeckJpaRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeckApplicationService {

    private final CardCatalogClient catalog;     // client para o card-service
    private final DeckJpaRepository deckRepo;    // JPA do deck-service

    @CircuitBreaker(name = "cardCatalog", fallbackMethod = "fallbackAdd")
    @Retry(name = "cardCatalog")
    @Transactional
    public DeckView addCard(String userId, Long deckId, Long cardId, int qty) throws ChangeSetPersister.NotFoundException {
        // 1) valida card no catálogo
        CardSummaryDTO card = catalog.getById(cardId);
        if (card == null) {
            throw new IllegalArgumentException("Card inexistente: " + cardId);
        }

        // 2) carrega deck (entidade de persistência) ou 404
        DeckEntity deck = deckRepo.findByOwnerIdAndId(userId, deckId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // (opcional) regras de negócio: limites, duplicatas, etc.
        // Exemplo simples: incrementa se já existe, senão adiciona
        DeckCardEntryEntity existing = deck.getEntries().stream()
                .filter(e -> e.getCardId().equals(cardId))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + Math.max(qty, 1));
        } else {
            DeckCardEntryEntity entry = new DeckCardEntryEntity();
            entry.setCardId(cardId);
            entry.setQuantity(Math.max(qty, 1));
            deck.addEntry(entry); // mantém bidirecional
        }

        // 3) persiste
        deckRepo.save(deck);

        // 4) monta a view (merge das entries com as infos do catálogo)
        // Como adicionamos 1 card, dá pra montar um Map mínimo:
        Map<Long, CardSummaryDTO> info = Map.of(cardId, card);
        return DeckView.from(deck, info);
    }

    // fallback do circuit breaker (assinatura deve casar com o método alvo + Throwable)
    private DeckView fallbackAdd(String userId, Long deckId, Long cardId, int qty, Throwable t) {
        throw new IllegalStateException("Catálogo de cartas indisponível no momento. Tente novamente mais tarde.", t);
    }
}