package com.odevpedro.yugiohcollections.deck.application.service.Impl;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardFeignClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.application.service.DeckApplicationService;
import com.odevpedro.yugiohcollections.deck.domain.exception.DeckValidationException;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.model.DeckZone;
import com.odevpedro.yugiohcollections.deck.domain.port.DeckRepositoryPort;
import com.odevpedro.yugiohcollections.deck.domain.service.DeckValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class DeckApplicationServiceImpl implements DeckApplicationService {

    private final DeckRepositoryPort deckRepository;
    private final CardFeignClient cardFeignClient;
    private final DeckValidator deckValidator;

    @Override
    public Deck createDeck(String ownerId, String name) {
        return deckRepository.save(Deck.of(ownerId, name));
    }

    @Override
    public List<Deck> listDecks(String ownerId) {
        return deckRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Deck getDeck(String ownerId, Long deckId) {
        return deckRepository.findByIdAndOwnerId(deckId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Deck nao encontrado para este usuario"));
    }

    @Override
    public Deck addCard(String ownerId, Long deckId, Long cardId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantidade deve ser > 0");

        Deck deck = getDeck(ownerId, deckId);
        deckValidator.validateAddCard(deck, cardId, quantity, DeckZone.MAIN);

        for (int i = 0; i < quantity; i++) {
            deck.addToMain(cardId);
        }
        return deckRepository.save(deck);
    }

    @Override
    public Deck addCardToZone(String ownerId, Long deckId, Long cardId, int quantity, DeckZone zone) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantidade deve ser > 0");

        Deck deck = getDeck(ownerId, deckId);
        deckValidator.validateAddCard(deck, cardId, quantity, zone);

        for (int i = 0; i < quantity; i++) {
            switch (zone) {
                case MAIN -> deck.addToMain(cardId);
                case EXTRA -> deck.addToExtra(cardId);
                case SIDE -> deck.addToSide(cardId);
            }
        }
        return deckRepository.save(deck);
    }

    @Override
    public Deck removeCard(String ownerId, Long deckId, Long cardId, String zone) {
        Deck deck = getDeck(ownerId, deckId);

        DeckZone deckZone = DeckZone.valueOf(zone.toUpperCase());
        deckValidator.validateRemoveCard(deck, cardId, deckZone);

        switch (deckZone) {
            case MAIN  -> deck.removeFromMain(cardId);
            case EXTRA -> deck.removeFromExtra(cardId);
            case SIDE  -> deck.removeFromSide(cardId);
        }

        return deckRepository.save(deck);
    }

    @Override
    public DeckView getDeckWithCards(String ownerId, Long deckId) {
        Deck deck = getDeck(ownerId, deckId);

        List<Long> distinctIds = Stream.of(deck.getMainDeck(), deck.getExtraDeck(), deck.getSideDeck())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .filter(id -> id != null && id > 0)
                .toList();

        Map<Long, Long> cardCounts = Stream.of(deck.getMainDeck(), deck.getExtraDeck(), deck.getSideDeck())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toMap(id -> id, id -> 1L, Long::sum));

        List<CardSummaryDTO> enriched;
        try {
            enriched = cardFeignClient.findCardsByIds(distinctIds).stream()
                    .map(c -> CardSummaryDTO.builder()
                            .cardId(c.getCardId())
                            .name(c.getName())
                            .type(c.getType())
                            .imageUrl(c.getImageUrl())
                            .description(c.getDescription())
                            .atk(c.getAtk())
                            .def(c.getDef())
                            .level(c.getLevel())
                            .quantity(cardCounts.getOrDefault(c.getCardId(), 1L).intValue())
                            .build())
                    .toList();
        } catch (Exception e) {
            enriched = List.of();
        }

        DeckView view = DeckView.from(deck, enriched);

        view.setValidation(deckValidator.validateDeck(deck));

        return view;
    }

    @Override
    public void deleteDeck(String ownerId, Long deckId) {
        getDeck(ownerId, deckId);
        deckRepository.deleteByIdAndOwnerId(deckId, ownerId);
    }
}
