package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.domain.model.Card;
import com.odevpedro.yugiohcollections.deck.domain.model.ports.CardSearchPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class ExternalCardController {

    private final CardSearchPort cardSearchPort;

    public ExternalCardController(CardSearchPort cardSearchPort) {
        this.cardSearchPort = cardSearchPort;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Card>> searchByName(@RequestParam String name) {
        List<Card> cards = cardSearchPort.searchByName(name);
        return ResponseEntity.ok(cards);
    }
}