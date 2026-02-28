package com.odevpedro.yugiohcollections.card.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ygopro-api", url = "${ygopro.url:https://db.ygoprodeck.com/api/v7}")
public interface YgoProFeignClient {

    @GetMapping("/cardinfo.php")
    JsonNode getCardsByName(@RequestParam("name") String name);

    @GetMapping("/cardinfo.php")
    JsonNode getCardsByFuzzy(@RequestParam("fname") String fname);

    @GetMapping("/cardinfo.php")
    JsonNode getCardsByType(@RequestParam("type") String type);

    @GetMapping("/cardinfo.php")
    JsonNode getCardsByTypeAndRace(@RequestParam("type") String type,
                                   @RequestParam("race") String race);

    @GetMapping("/cardinfo.php")
    JsonNode getCardsByIds(@RequestParam("id") String idsCsv);

}