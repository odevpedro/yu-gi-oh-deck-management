package com.odevpedro.yugiohcollections.deck.adapter.out.external;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@FeignClient(name = "ygoProClient", url = "https://db.ygoprodeck.com/api/v7")
public interface YgoProFeignClient {

    @GetMapping("/cardinfo.php")
    JsonNode getCardsByName(@RequestParam("name") String name);
}