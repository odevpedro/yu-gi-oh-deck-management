package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.application.service.DeckApplicationService;
import com.odevpedro.yugiohcollections.deck.application.service.DeckExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckApplicationService service;
    private final DeckExportService exportService;

    @PostMapping
    public DeckView create(Authentication auth,
                           @RequestBody CreateDeckRequest body) {
        String userId = (String) auth.getDetails();
        return DeckView.simple(service.createDeck(userId, body.name()));
    }

    @GetMapping
    public List<DeckView> list(Authentication auth) {
        String userId = (String) auth.getDetails();
        return service.listDecks(userId).stream()
                .map(DeckView::simple)
                .toList();
    }

    @GetMapping("/{deckId}")
    public DeckView get(Authentication auth,
                        @PathVariable Long deckId) {
        return DeckView.simple(service.getDeck((String) auth.getDetails(), deckId));
    }

    @GetMapping("/{deckId}/full")
    public DeckView getFull(Authentication auth,
                            @PathVariable Long deckId) {
        return service.getDeckWithCards((String) auth.getDetails(), deckId);
    }

    @PostMapping("/{deckId}/cards")
    public DeckView addCard(Authentication auth,
                            @PathVariable Long deckId,
                            @RequestBody AddCardRequest body) {
        return DeckView.simple(service.addCard((String) auth.getDetails(), deckId, body.cardId(), body.quantity()));
    }

    @DeleteMapping("/{deckId}/cards")
    public DeckView removeCard(Authentication auth,
                               @PathVariable Long deckId,
                               @RequestBody RemoveCardRequest body) {
        return DeckView.simple(service.removeCard((String) auth.getDetails(), deckId, body.cardId(), body.zone()));
    }

    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(Authentication auth,
                                           @PathVariable Long deckId) {
        service.deleteDeck((String) auth.getDetails(), deckId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deckId}/export")
    public ResponseEntity<byte[]> exportDeck(@PathVariable Long deckId,
                                             Authentication auth) {
        String userId  = (String) auth.getDetails();
        String content = exportService.exportAsYdk(deckId, userId);
        byte[] bytes   = content.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"deck-" + deckId + ".ydk\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(bytes.length)
                .body(bytes);
    }

    public record CreateDeckRequest(String name) {}
    public record AddCardRequest(Long cardId, int quantity) {}
    public record RemoveCardRequest(Long cardId, String zone) {}
}