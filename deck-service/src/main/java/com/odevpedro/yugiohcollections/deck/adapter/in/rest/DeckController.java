package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.application.service.CreateDeckUseCase;
import com.odevpedro.yugiohcollections.deck.application.service.FindDeckByIdUseCase;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/decks")
public class DeckController {

    private final CreateDeckUseCase createDeckUseCase;
    private final FindDeckByIdUseCase findDeckByIdUseCase;

    public DeckController(CreateDeckUseCase createDeckUseCase, FindDeckByIdUseCase findDeckByIdUseCase) {
        this.createDeckUseCase = createDeckUseCase;
        this.findDeckByIdUseCase = findDeckByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<Deck> create(@RequestBody Deck deck) {
        return ResponseEntity.ok(createDeckUseCase.execute(deck));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, @RequestParam String ownerId) {
        Optional<Deck> found = findDeckByIdUseCase.execute(id, ownerId);
        return found.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("Deck não encontrado ou acesso negado"));
    }
}
