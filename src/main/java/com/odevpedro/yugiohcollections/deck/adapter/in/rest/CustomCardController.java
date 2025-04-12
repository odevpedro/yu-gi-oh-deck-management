package com.odevpedro.yugiohcollections.deck.adapter.in.rest;

import com.odevpedro.yugiohcollections.deck.application.dto.CardInputDTO;
import com.odevpedro.yugiohcollections.deck.application.service.SaveCardUseCase;
import com.odevpedro.yugiohcollections.deck.domain.model.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
public class CustomCardController {

    private final SaveCardUseCase saveCardUseCase;

    public CustomCardController(SaveCardUseCase saveCardUseCase) {
        this.saveCardUseCase = saveCardUseCase;
    }

    @PostMapping("/custom")
    public ResponseEntity<Card> createCustomCard(@RequestBody CardInputDTO dto) {
        Card savedCard = saveCardUseCase.execute(dto);
        return ResponseEntity.ok(savedCard);
    }
}