package com.odevpedro.yugiohcollections.deck.main.java.com.odevpedro.deck.deckservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/decks")
public class DeckController {

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Deck service funcionando!");
    }
}
