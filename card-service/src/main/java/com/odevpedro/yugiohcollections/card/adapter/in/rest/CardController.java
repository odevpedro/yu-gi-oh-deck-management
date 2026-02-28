package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.application.dto.CardSummaryDTO;
import com.odevpedro.yugiohcollections.card.application.service.SearchCardsUseCase;
import com.odevpedro.yugiohcollections.card.domain.model.Card;
import com.odevpedro.yugiohcollections.card.domain.model.ports.ExternalCardQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final ExternalCardQueryPort externalQueryPort;
    private final SearchCardsUseCase searchCardsUseCase;

    // ===== Busca pública — usada pelo cliente final =====

    @GetMapping("/cards/search")
    public ResponseEntity<Page<CardResponseDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String fname,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 50, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(searchCardsUseCase.search(name, fname, type, pageable));
    }

    // ===== Busca interna — usada pelo deck-service via Feign =====

    @GetMapping("/internal/cards/{id}")
    public ResponseEntity<CardSummaryDTO> byId(@PathVariable Long id) {
        return externalQueryPort.findByIds(List.of(id)).stream()
                .findFirst()
                .map(this::toSummary)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/internal/cards")
    public ResponseEntity<List<CardSummaryDTO>> byIds(@RequestParam("ids") List<Long> ids) {
        List<CardSummaryDTO> result = externalQueryPort.findByIds(ids).stream()
                .map(this::toSummary)
                .toList();
        return ResponseEntity.ok(result);
    }

    private CardSummaryDTO toSummary(Card c) {
        return new CardSummaryDTO(
                c.getId(),
                c.getName(),
                c.getType() != null ? c.getType().name() : null,
                c.getImageUrl(),
                c.getDescription()
        );
    }
}