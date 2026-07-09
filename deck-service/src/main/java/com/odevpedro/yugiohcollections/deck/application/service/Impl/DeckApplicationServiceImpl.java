package com.odevpedro.yugiohcollections.deck.application.service.Impl;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.adapter.out.messaging.DeckSyncEventPublisher;
import com.odevpedro.yugiohcollections.deck.application.service.DeckApplicationService;
import com.odevpedro.yugiohcollections.deck.application.service.DeckCardCatalogService;
import com.odevpedro.yugiohcollections.deck.domain.exception.DeckValidationException;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import com.odevpedro.yugiohcollections.deck.domain.model.DeckZone;
import com.odevpedro.yugiohcollections.deck.domain.port.DeckRepositoryPort;
import com.odevpedro.yugiohcollections.deck.domain.service.DeckValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class DeckApplicationServiceImpl implements DeckApplicationService {

    private final DeckRepositoryPort deckRepository;
    private final DeckCardCatalogService deckCardCatalogService;
    private final DeckValidator deckValidator;
    private final DeckSyncEventPublisher deckSyncEventPublisher;

    @Override
    public Deck createDeck(String ownerId, String name) {
        Deck saved = deckRepository.save(Deck.of(ownerId, name));
        deckSyncEventPublisher.publish("CREATED", saved);
        return saved;
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
        Deck saved = deckRepository.save(deck);
        deckSyncEventPublisher.publish("UPDATED", saved);
        return saved;
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

        Deck saved = deckRepository.save(deck);
        deckSyncEventPublisher.publish("UPDATED", saved);
        return saved;
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

        Map<Long, CardSummaryDTO> cardInfoById = deckCardCatalogService.loadCardInfo(distinctIds, cardCounts);

        DeckView view = DeckView.from(deck, cardInfoById);

        view.setValidation(deckValidator.validateDeck(deck));

        return view;
    }

    @Override
    public DeckView importDeck(String ownerId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo .ydk e obrigatorio");
        }

        Deck deck = parseYdk(file, ownerId);
        Deck saved = deckRepository.save(deck);
        deckSyncEventPublisher.publish("IMPORTED", saved);

        List<Long> distinctIds = Stream.of(saved.getMainDeck(), saved.getExtraDeck(), saved.getSideDeck())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        Map<Long, Long> cardCounts = Stream.of(saved.getMainDeck(), saved.getExtraDeck(), saved.getSideDeck())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toMap(id -> id, id -> 1L, Long::sum));

        Map<Long, CardSummaryDTO> cardInfoById = deckCardCatalogService.loadCardInfo(distinctIds, cardCounts);
        DeckView view = DeckView.from(saved, cardInfoById);
        var validation = deckValidator.validateDeck(saved);
        view.setValidation(validation);

        List<String> validationErrors = new java.util.ArrayList<>(view.getValidationErrors());
        Set<Long> missingCardIds = new LinkedHashSet<>(distinctIds);
        missingCardIds.removeAll(cardInfoById.keySet());
        if (!missingCardIds.isEmpty()) {
            validationErrors.add("Cartas nao encontradas no card-service: " + missingCardIds);
            view.setValid(false);
            view.setValidationErrors(validationErrors);
        }

        return view;
    }

    @Override
    public void deleteDeck(String ownerId, Long deckId) {
        Deck deck = getDeck(ownerId, deckId);
        deckSyncEventPublisher.publish("DELETED", deck);
        deckRepository.deleteByIdAndOwnerId(deckId, ownerId);
    }

    private Deck parseYdk(MultipartFile file, String ownerId) {
        List<Long> main = new java.util.ArrayList<>();
        List<Long> extra = new java.util.ArrayList<>();
        List<Long> side = new java.util.ArrayList<>();

        DeckZone currentZone = null;
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            for (String rawLine : content.split("\\R")) {
                String line = rawLine.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if ("#main".equalsIgnoreCase(line)) {
                    currentZone = DeckZone.MAIN;
                    continue;
                }
                if ("#extra".equalsIgnoreCase(line)) {
                    currentZone = DeckZone.EXTRA;
                    continue;
                }
                if ("!side".equalsIgnoreCase(line)) {
                    currentZone = DeckZone.SIDE;
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                if (currentZone == null) {
                    continue;
                }

                try {
                    Long cardId = Long.parseLong(line);
                    if (cardId <= 0) {
                        throw new IllegalArgumentException("ID de carta invalido: " + line);
                    }
                    switch (currentZone) {
                        case MAIN -> main.add(cardId);
                        case EXTRA -> extra.add(cardId);
                        case SIDE -> side.add(cardId);
                    }
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Linha invalida no .ydk: " + line);
                }
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Nao foi possivel ler o arquivo .ydk", ex);
        }

        return Deck.builder()
                .ownerId(ownerId)
                .name(stripExtension(file.getOriginalFilename()))
                .mainDeck(main)
                .extraDeck(extra)
                .sideDeck(side)
                .build();
    }

    private String stripExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "Imported Deck";
        }
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
}
