package com.odevpedro.yugiohcollections.proxy.adapter.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "deck-service", url = "${deck-service.url}")
public interface DeckFeignClient {

    @GetMapping("/decks/{deckId}/full")
    DeckViewDTO getDeckWithCards(@PathVariable Long deckId);
}