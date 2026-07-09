package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardFeignClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeckCardCatalogService {

    private final CardFeignClient cardFeignClient;

    @CircuitBreaker(name = "cardCatalog", fallbackMethod = "loadCardInfoFallback")
    public Map<Long, CardSummaryDTO> loadCardInfo(List<Long> cardIds, Map<Long, Long> cardCounts) {
        Map<Long, CardSummaryDTO> cardInfoById = new HashMap<>();

        if (cardIds == null || cardIds.isEmpty()) {
            return cardInfoById;
        }

        cardFeignClient.findCardsByIds(cardIds).forEach(card -> {
            card.setQuantity(cardCounts.getOrDefault(card.getCardId(), 1L).intValue());
            cardInfoById.put(card.getCardId(), card);
        });

        return cardInfoById;
    }

    @SuppressWarnings("unused")
    public Map<Long, CardSummaryDTO> loadCardInfoFallback(List<Long> cardIds,
                                                          Map<Long, Long> cardCounts,
                                                          Throwable throwable) {
        log.warn("card-service indisponivel; retornando deck sem detalhes das cartas: {}", throwable.getMessage());
        return new HashMap<>();
    }
}
