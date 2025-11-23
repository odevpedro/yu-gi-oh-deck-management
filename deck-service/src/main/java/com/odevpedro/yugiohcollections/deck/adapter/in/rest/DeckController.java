package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardFeignClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.application.service.*;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckApplicationService service;
    private final CardFeignClient cardFeignClient;

    @PostMapping
    public DeckView create(@AuthenticationPrincipal Jwt jwt,
                           @RequestBody CreateDeckRequest body) {
        String userId = extractUserId(jwt);
        var deck = service.createDeck(userId, body.name());
        return DeckView.simple(deck);
    }

    @GetMapping
    public List<DeckView> list(@AuthenticationPrincipal Jwt jwt) {
        String userId = extractUserId(jwt);
        return service.listDecks(userId).stream()
                .map(DeckView::simple)
                .toList();
    }

    @GetMapping("/{deckId}")
    public DeckView get(@AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long deckId) throws Exception {
        String userId = extractUserId(jwt);
        return DeckView.simple(service.getDeck(userId, deckId));
    }

    @PostMapping("/{deckId}/cards")
    public DeckView addCard(@AuthenticationPrincipal Jwt jwt,
                            @PathVariable Long deckId,
                            @RequestBody AddCardRequest body) throws Exception {
        String userId = extractUserId(jwt);
        return DeckView.simple(service.addCard(userId, deckId, body.cardId(), body.quantity()));
    }

    @GetMapping("/{deckId}/full")
    public DeckView getFull(@AuthenticationPrincipal Jwt jwt,
                            @PathVariable Long deckId) throws Exception {
        String userId = extractUserId(jwt);
        Deck deck = service.getDeck(userId, deckId);

        List<Long> ids = deck.allCardIds().stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        System.out.println(" IDs a serem buscados (limpos): " + ids);

        List<CardSummaryDTO> enrichedCards;
        try {
            enrichedCards = cardFeignClient.findCardsByIds(ids);
        } catch (Exception e) {
            System.err.println("Erro ao buscar cartas no card-service: " + e.getMessage());
            enrichedCards = List.of();
        }

        return DeckView.from(deck, enrichedCards);
    }

    private String extractUserId(Jwt jwt) {
        if (jwt == null) {
            return "dev-user";
        }
        Object v = jwt.getClaim("sub");
        if (v == null) throw new IllegalStateException("JWT sem 'sub'");
        return v.toString();
    }

    public record CreateDeckRequest(String name) {}
    public record AddCardRequest(Long cardId, int quantity) {}
}