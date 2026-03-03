package com.odevpedro.yugiohcollections.deck;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableFeignClients(basePackages = "com.odevpedro.yugiohcollections.deck.adapter.out.external")
@ComponentScan(basePackages = {
        "com.odevpedro.yugiohcollections.deck",
        "com.odevpedro.yugiohcollections.shared"
})
public class DeckServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeckServiceApplication.class, args);
    }
}