package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import org.springframework.web.bind.annotation.RequestParam;

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
