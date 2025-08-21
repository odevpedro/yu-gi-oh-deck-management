package com.odevpedro.yugiohcollections.deck;

import com.odevpedro.yugiohcollections.deck.adapter.out.cardcatalog.CardCatalogClient;
import com.odevpedro.yugiohcollections.deck.adapter.out.external.CardFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackageClasses = { CardFeignClient.class, CardCatalogClient.class })
public class DeckServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeckServiceApplication.class, args);
    }
}