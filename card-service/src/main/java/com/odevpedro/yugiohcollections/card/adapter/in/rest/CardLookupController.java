package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.application.dto.CardSummaryDTO;
import com.odevpedro.yugiohcollections.card.application.service.SearchCardsUseCase;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.CardQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/cards")
@RequiredArgsConstructor
public class CardLookupController {

    private final CardQueryPort cardQueryPort;
    private final SearchCardsUseCase searchCardsUseCase; // ou usecase que consulta oficial+cache

    @GetMapping("/{id}")
    public ResponseEntity<CardSummaryDTO> byId(@PathVariable Long id){
        return cardQueryPort.findAllByIds(List.of(id)).stream().findFirst()
                .map(this::toSummary)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CardSummaryDTO>> byIds(@RequestParam("ids") List<Long> ids){
        List<Card> cards = cardQueryPort.findAllByIds(ids);
        List<CardSummaryDTO> result = cards.stream()
                .map(this::toCardSummaryDTO)
                .toList();
        return ResponseEntity.ok(result); // nunca retorna 404!
    }

    private CardSummaryDTO toCardSummaryDTO(Card card) {
        return new CardSummaryDTO(
                card.getId(),
                card.getName(),
                card.getType() != null ? card.getType().name() : null,
                card.getImageUrl(),
                card.getDescription()
        );
    }

    private CardSummaryDTO toSummary(Card c){
        return new CardSummaryDTO(c.getId(), c.getName(), c.getType().name(), c.getImageUrl(), c.getDescription());
    }


}

