package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.application.dto.DeckWithCardsDTO;
import com.odevpedro.yugiohcollections.deck.application.service.*;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckApplicationService service;

    // cria um deck só com o nome
    @PostMapping
    public DeckView create(@AuthenticationPrincipal OAuth2ResourceServerProperties.Jwt jwt,
                           @RequestBody CreateDeckRequest body) {
        String userId = extractUserId(jwt);
        var deck = service.createDeck(userId, body.name());
        return DeckView.simple(deck); // retorna id/name/owner sem cartas
    }

    // lista decks do usuário autenticado
    @GetMapping
    public List<DeckView> list(@AuthenticationPrincipal OAuth2ResourceServerProperties.Jwt jwt) {
        String userId = extractUserId(jwt);
        return service.listDecks(userId).stream()
                .map(DeckView::simple)
                .toList();
    }

    // detalhe de um deck (sem resolver cartas)
    @GetMapping("/{deckId}")
    public DeckView get(@AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long deckId) throws Exception {
        String userId = extractUserId(jwt);
        return service.getDeck(userId, deckId);
    }

    // adiciona carta ao deck
    @PostMapping("/{deckId}/cards")
    public DeckView addCard(@AuthenticationPrincipal Jwt jwt,
                            @PathVariable Long deckId,
                            @RequestBody AddCardRequest body) throws Exception {
        String userId = extractUserId(jwt);
        return service.addCard(userId, deckId, body.cardId(), body.quantity());
    }

    /* ===== helpers ===== */

    // ajuste o claim conforme seu token (ex.: "sub", "user_id", "preferred_username"...)
    private String extractUserId(Jwt jwt) {
        Object v = jwt.getClaim("sub");
        if (v == null) throw new IllegalStateException("JWT sem 'sub'");
        return v.toString();
    }

    public record CreateDeckRequest(String name) {}
    public record AddCardRequest(Long cardId, int quantity) {}
}