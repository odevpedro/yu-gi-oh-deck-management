package com.odevpedro.yugiohcollections.deck.src.application.service;

import com.odevpedro.yugiohcollections.deck.src.adapter.out.external.CardFeignClient;
import com.odevpedro.yugiohcollections.deck.src.adapter.out.external.CardResponseDTO;
import com.odevpedro.yugiohcollections.deck.src.application.dto.DeckWithCardsDTO;
import com.odevpedro.yugiohcollections.deck.src.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.src.domain.port.DeckRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindDeckWithCardsUseCase {

    private final DeckRepositoryPort repository;
    private final CardFeignClient cardFeignClient;

    public FindDeckWithCardsUseCase(DeckRepositoryPort repository, CardFeignClient cardFeignClient) {
        this.repository = repository;
        this.cardFeignClient = cardFeignClient;
    }

    public DeckWithCardsDTO execute(Long deckId, String ownerId) {
        Deck deck = repository.findByIdAndOwner(deckId, ownerId)
                .orElseThrow(() -> new RuntimeException("Deck não encontrado"));

        List<CardResponseDTO> main = cardFeignClient.findCardsByIds(deck.getMainDeck());
        List<CardResponseDTO> extra = cardFeignClient.findCardsByIds(deck.getExtraDeck());
        List<CardResponseDTO> side = cardFeignClient.findCardsByIds(deck.getSideDeck());

        return new DeckWithCardsDTO(deck.getId(), deck.getName(), deck.getOwnerId(), main, extra, side);
    }
}
