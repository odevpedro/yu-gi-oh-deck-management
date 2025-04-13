package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.application.service.ListCustomCardsUseCase;
import com.odevpedro.yugiohcollections.deck.domain.model.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CustomCardQueryController {

    private final ListCustomCardsUseCase listCustomCardsUseCase;

    public CustomCardQueryController(ListCustomCardsUseCase listCustomCardsUseCase) {
        this.listCustomCardsUseCase = listCustomCardsUseCase;
    }

    @GetMapping("/custom")
    public ResponseEntity<List<Card>> listByOwner(@RequestParam String ownerId) {
        List<Card> cards = listCustomCardsUseCase.findAllByOwner(ownerId);
        return ResponseEntity.ok(cards);
    }
}