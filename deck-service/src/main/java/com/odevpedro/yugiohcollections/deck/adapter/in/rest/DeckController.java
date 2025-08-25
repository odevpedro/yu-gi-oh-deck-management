package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.application.service.*;
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

    // cria um deck só com o nome
    @PostMapping
    public DeckView create(@AuthenticationPrincipal Jwt jwt,
                           @RequestBody CreateDeckRequest body) {
        String userId = extractUserId(jwt);
        var deck = service.createDeck(userId, body.name());
        return DeckView.simple(deck);
    }

    // lista decks do usuário autenticado
    @GetMapping
    public List<DeckView> list(@AuthenticationPrincipal Jwt jwt) {
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
        return DeckView.simple(service.getDeck(userId, deckId));
    }

    // adiciona carta ao deck
    @PostMapping("/{deckId}/cards")
    public DeckView addCard(@AuthenticationPrincipal Jwt jwt,
                            @PathVariable Long deckId,
                            @RequestBody AddCardRequest body) throws Exception {
        String userId = extractUserId(jwt);
        return DeckView.simple(service.addCard(userId, deckId, body.cardId(), body.quantity()));
    }

    /* ===== helpers ===== */

    private String extractUserId(Jwt jwt) {
        if (jwt == null) {
            return "dev-user"; // fallback para testes locais sem token
        }
        Object v = jwt.getClaim("sub");
        if (v == null) throw new IllegalStateException("JWT sem 'sub'");
        return v.toString();
    }

    public record CreateDeckRequest(String name) {}
    public record AddCardRequest(Long cardId, int quantity) {}
}