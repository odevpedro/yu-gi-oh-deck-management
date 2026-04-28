package com.odevpedro.yugiohcollections.shared.security;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "auth-service", url = "${auth.service.url:http://localhost:8086}")
public interface TokenValidationClient {

    @GetMapping("/internal/validate")
    Map<String, Object> validateToken(@RequestHeader("Authorization") String authorization);
}