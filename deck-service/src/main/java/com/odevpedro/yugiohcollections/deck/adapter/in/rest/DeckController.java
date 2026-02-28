package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.application.service.DeckApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckApplicationService service;

    @PostMapping
    public DeckView create(@AuthenticationPrincipal Jwt jwt,
                           @RequestBody CreateDeckRequest body) {
        return DeckView.simple(service.createDeck(extractUserId(jwt), body.name()));
    }

    @GetMapping
    public List<DeckView> list(@AuthenticationPrincipal Jwt jwt) {
        return service.listDecks(extractUserId(jwt)).stream()
                .map(DeckView::simple)
                .toList();
    }

    @GetMapping("/{deckId}")
    public DeckView get(@AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long deckId) {
        return DeckView.simple(service.getDeck(extractUserId(jwt), deckId));
    }

    @GetMapping("/{deckId}/full")
    public DeckView getFull(@AuthenticationPrincipal Jwt jwt,
                            @PathVariable Long deckId) {
        return service.getDeckWithCards(extractUserId(jwt), deckId);
    }

    @PostMapping("/{deckId}/cards")
    public DeckView addCard(@AuthenticationPrincipal Jwt jwt,
                            @PathVariable Long deckId,
                            @RequestBody AddCardRequest body) {
        return DeckView.simple(service.addCard(extractUserId(jwt), deckId, body.cardId(), body.quantity()));
    }

    @DeleteMapping("/{deckId}/cards")
    public DeckView removeCard(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long deckId,
                               @RequestBody RemoveCardRequest body) {
        return DeckView.simple(service.removeCard(extractUserId(jwt), deckId, body.cardId(), body.zone()));
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable Long deckId) {
        service.deleteDeck(extractUserId(jwt), deckId);
        return ResponseEntity.noContent().build();
    }

    private String extractUserId(Jwt jwt) {
        if (jwt == null) return "dev-user";
        Object v = jwt.getClaim("sub");
        if (v == null) throw new IllegalStateException("JWT sem 'sub'");
        return v.toString();
    }

    public record CreateDeckRequest(String name) {}
    public record AddCardRequest(Long cardId, int quantity) {}
    public record RemoveCardRequest(Long cardId, String zone) {}
}