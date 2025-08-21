package com.odevpedro.yugiohcollections.deck.application.service;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardFeignClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardResponseDTO;
import com.odevpedro.yugiohcollections.deck.application.dto.DeckWithCardsDTO;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.port.DeckRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FindDeckWithCardsUseCase {

    private final DeckRepositoryPort repository;
    private final CardFeignClient cardFeignClient;

    public DeckWithCardsDTO execute(Long deckId, String ownerId) {
        Deck deck = repository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new RuntimeException("Deck não encontrado"));

        List<CardResponseDTO> main = cardFeignClient.findCardsByIds(deck.getMainDeck());
        List<CardResponseDTO> extra = cardFeignClient.findCardsByIds(deck.getExtraDeck());
        List<CardResponseDTO> side = cardFeignClient.findCardsByIds(deck.getSideDeck());

        return new DeckWithCardsDTO(deck.getId(), deck.getName(), deck.getOwnerId(), main, extra, side);
    }
}