package com.odevpedro.yugiohcollections.community.adapter.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "duel-service", url = "${duel-service.url:http://localhost:8084}")
public interface DuelFeignClient {

    @PostMapping("/api/duels")
    DuelResponseDTO createDuel(@RequestBody CreateDuelRequestDTO request);
}
