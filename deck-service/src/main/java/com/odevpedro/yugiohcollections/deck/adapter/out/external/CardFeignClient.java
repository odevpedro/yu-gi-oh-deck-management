package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "card-service", url = "${card-service.url:http://localhost:8080}")
public interface CardFeignClient {

    @GetMapping("/cards/internal")
    List<CardSummaryDTO> findCardsByIds(@RequestParam("ids") List<Long> ids);
}
