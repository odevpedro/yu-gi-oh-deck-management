package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.application.dto.DeckWithCardsDTO;
import com.odevpedro.yugiohcollections.deck.application.service.CreateDeckUseCase;
import com.odevpedro.yugiohcollections.deck.application.service.FindDeckByIdUseCase;
import com.odevpedro.yugiohcollections.deck.application.service.FindDeckWithCardsUseCase;
import com.odevpedro.yugiohcollections.deck.application.service.ListDecksByOwnerUseCase;
import com.odevpedro.yugiohcollections.deck.domain.model.Deck;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/decks")
public class DeckController {

    private final CreateDeckUseCase createDeckUseCase;
    private final FindDeckByIdUseCase findDeckByIdUseCase;
    private final ListDecksByOwnerUseCase listDecksByOwnerUseCase;
    private final FindDeckWithCardsUseCase findDeckWithCardsUseCase;

    public DeckController(CreateDeckUseCase createDeckUseCase, FindDeckByIdUseCase findDeckByIdUseCase, ListDecksByOwnerUseCase listDecksByOwnerUseCase, FindDeckWithCardsUseCase findDeckWithCardsUseCase) {
        this.createDeckUseCase = createDeckUseCase;
        this.findDeckByIdUseCase = findDeckByIdUseCase;
        this.listDecksByOwnerUseCase = listDecksByOwnerUseCase;
        this.findDeckWithCardsUseCase = findDeckWithCardsUseCase;
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

    //aqui só busca por dono
    @GetMapping
    public ResponseEntity<List<Deck>> listByOwner(@RequestParam String ownerId) {
        return ResponseEntity.ok(listDecksByOwnerUseCase.execute(ownerId));
    }

    @GetMapping("/{id}/with-cards")
    public ResponseEntity<?> findWithCards(@PathVariable Long id, @RequestParam String ownerId) {
        try {
            DeckWithCardsDTO dto = findDeckWithCardsUseCase.execute(id, ownerId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
