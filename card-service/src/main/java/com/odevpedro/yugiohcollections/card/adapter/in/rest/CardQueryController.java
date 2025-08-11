package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import com.odevpedro.yugiohcollections.card.adapter.out.dto.CardResponseDTO;
import com.odevpedro.yugiohcollections.card.application.dto.CardSimpleResponse;
import com.odevpedro.yugiohcollections.card.application.service.FindCardsByIdsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardQueryController {

    //Essa classe só busca aquilo que está definido localmente

    private final FindCardsByIdsUseCase findCardsByIdsUseCase;

    public CardQueryController(FindCardsByIdsUseCase findCardsByIdsUseCase) {
        this.findCardsByIdsUseCase = findCardsByIdsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> findByIds(@RequestParam(required = false) List<Long> ids) {
        List<CardResponseDTO> cards = findCardsByIdsUseCase.execute(ids);
        return ResponseEntity.ok(cards);
    }
}
