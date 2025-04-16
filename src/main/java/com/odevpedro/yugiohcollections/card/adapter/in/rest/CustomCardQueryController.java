package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.application.CardFactory;
import com.odevpedro.yugiohcollections.card.application.dto.CardInputDTO;
import com.odevpedro.yugiohcollections.card.application.service.DeleteCardUseCase;
import com.odevpedro.yugiohcollections.card.application.service.ListCustomCardsUseCase;
import com.odevpedro.yugiohcollections.card.application.service.UpdateCardUseCase;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CustomCardQueryController {

    private final ListCustomCardsUseCase listCustomCardsUseCase;
    private final UpdateCardUseCase updateCardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;

    public CustomCardQueryController(ListCustomCardsUseCase listCustomCardsUseCase, UpdateCardUseCase updateCardUseCase, DeleteCardUseCase deleteCardUseCase) {
        this.listCustomCardsUseCase = listCustomCardsUseCase;
        this.updateCardUseCase = updateCardUseCase;
        this.deleteCardUseCase = deleteCardUseCase;
    }

    @GetMapping("/custom")
    public ResponseEntity<List<Card>> listByOwner(@RequestParam String ownerId) {
        List<Card> cards = listCustomCardsUseCase.findAllByOwner(ownerId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/custom/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody CardInputDTO dto
    ) {
        return CardFactory.fromDTO(dto)
                .flatMap(card -> updateCardUseCase.update(id, card))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Carta não encontrada ou acesso negado"));
    }

    @DeleteMapping("/custom/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestParam String ownerId
    ) {
        boolean deleted = deleteCardUseCase.delete(id, ownerId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Carta não encontrada ou acesso negado");
    }
}