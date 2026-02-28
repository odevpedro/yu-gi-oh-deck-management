package com.odevpedro.yugiohcollections.deck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.odevpedro.yugiohcollections.deck.adapter.out.external")
public class DeckServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeckServiceApplication.class, args);
    }
}