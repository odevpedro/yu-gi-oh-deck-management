package com.odevpedro.yugiohcollections.adapter.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.JsonNode;

@FeignClient(name = "ygopro-client", url = "https://db.ygoprodeck.com/api/v7")
public interface YgoProFeignClient {
    @GetMapping("/cardinfo.php")
    JsonNode getCardsByName(@RequestParam("name") String name);
}
