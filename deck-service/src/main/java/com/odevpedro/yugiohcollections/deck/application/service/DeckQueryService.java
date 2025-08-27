package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardFeignClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.port.DeckRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DeckQueryService {

    private final DeckRepositoryPort deckRepository;
    private final CardFeignClient cardClient;

    public DeckView getFull(String ownerId, Long deckId) {
        Deck deck = deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck não encontrado"));

        // Conta quantas vezes cada cardId aparece (nas três zonas)
        Map<Long, Integer> cardCounts = Stream.of(
                        deck.getMainDeck(), deck.getExtraDeck(), deck.getSideDeck()
                )
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        id -> id,
                        id -> 1,
                        Integer::sum
                ));

        List<Long> distinctIds = new ArrayList<>(cardCounts.keySet());

        // Busca dados via Feign
        List<CardSummaryDTO> baseCards = cardClient.findCardsByIds(distinctIds);

        // Enriquecer com quantity
        List<CardSummaryDTO> enriched = baseCards.stream()
                .map(c -> CardSummaryDTO.builder()
                        .cardId(c.getCardId())
                        .name(c.getName())
                        .type(c.getType())
                        .imageUrl(c.getImageUrl())
                        .description(c.getDescription()) // se disponível
                        .quantity(cardCounts.getOrDefault(c.getCardId(), 1))
                        .build())
                .toList();

        return DeckView.from(deck, enriched);
    }
}