package com.odevpedro.yugiohcollections.deck.adapter.out.cardcatalog;

import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "card-catalog", url = "${card-service.url}")
public interface CardCatalogClient {

    @GetMapping("/internal/cards/{id}")
    CardSummaryDTO getById(@PathVariable("id") Long id);

    @GetMapping("/internal/cards")
    List<CardSummaryDTO> getByIds(@RequestParam("ids") List<Long> ids);
}