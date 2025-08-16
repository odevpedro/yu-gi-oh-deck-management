package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.application.CardFactory;
import com.odevpedro.yugiohcollections.card.application.dto.CardInputDTO;
import com.odevpedro.yugiohcollections.card.application.service.DeleteCardUseCase;
import com.odevpedro.yugiohcollections.card.application.service.ListCustomCardsUseCase;
import com.odevpedro.yugiohcollections.card.application.service.SaveCardUseCase;
import com.odevpedro.yugiohcollections.card.application.service.UpdateCardUseCase;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/cards")
@AllArgsConstructor
public class CustomCardQueryController {

    private final ListCustomCardsUseCase listCustomCardsUseCase;
    private final UpdateCardUseCase updateCardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;
    private final SaveCardUseCase saveCardUseCase;


    @GetMapping("/test/{ownerId}")
    public ResponseEntity<List<Card>> listByOwner(@RequestParam String ownerId) {
        List<Card> cards = listCustomCardsUseCase.findAllByOwner(ownerId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/test/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody CardInputDTO dto
    ) {
        return CardFactory.fromDTO(dto)
                .flatMap(card -> updateCardUseCase.update(id, card))
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck não encontrado"));
    }

    @DeleteMapping("/test{id}")
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


    @PostMapping("/test")
    public ResponseEntity<Card> createCustomCard(@RequestBody @Validated CardInputDTO dto) {
        Card savedCard = saveCardUseCase.execute(dto);
        return ResponseEntity.ok(savedCard);
    }

}