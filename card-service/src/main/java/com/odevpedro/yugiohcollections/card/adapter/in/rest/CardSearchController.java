package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.application.service.SearchCardsUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
public class CardSearchController {

    private final SearchCardsUseCase useCase;

    public CardSearchController(SearchCardsUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CardResponseDTO>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String fname, // fuzzy opcional
            @RequestParam(required = false) String type,  // MONSTER|SPELL|TRAP
            @PageableDefault(size = 50, sort = "name") Pageable pageable
    ) {
        Page<CardResponseDTO> page = useCase.search(name, fname, type, pageable);
        return ResponseEntity.ok(page);
    }
}
