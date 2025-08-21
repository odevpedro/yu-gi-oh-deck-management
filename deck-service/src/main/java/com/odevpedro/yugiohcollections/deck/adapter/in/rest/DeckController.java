package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.DeckView;
import com.odevpedro.yugiohcollections.deck.application.dto.DeckWithCardsDTO;
import com.odevpedro.yugiohcollections.deck.application.service.*;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users/{ownerId}/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckApplicationService service;

    @PostMapping("/{deckId}/cards")
    public DeckView add(@PathVariable("ownerId") String ownerId,
                        @PathVariable("deckId") Long deckId,
                        @RequestBody AddCardRequest body)
            throws ChangeSetPersister.NotFoundException {
        return service.addCard(ownerId, deckId, body.cardId(), body.quantity());
    }
}

record AddCardRequest(Long cardId, int quantity) {}
