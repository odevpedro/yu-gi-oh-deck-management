package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.application.dto.CardSummaryDTO;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CardLookupController.java (card-service)
@RestController
@RequestMapping("/internal/cards")
@RequiredArgsConstructor
public class CardLookupController {

    private final CardQueryPort cardQueryPort; // ou usecase que consulta oficial+cache

    @GetMapping("/{id}")
    public ResponseEntity<CardSummaryDTO> byId(@PathVariable Long id){
        return cardQueryPort.findAllByIds(List.of(id)).stream().findFirst()
                .map(this::toSummary)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CardSummaryDTO> byIds(@RequestParam List<Long> ids){
        return cardQueryPort.findAllByIds(ids).stream()
                .map(this::toSummary)
                .toList();
    }

    private CardSummaryDTO toSummary(Card c){
        return new CardSummaryDTO(c.getId(), c.getName(), c.getType().name(), c.getImageUrl());
    }
}

