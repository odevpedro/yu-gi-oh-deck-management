package com.odevpedro.yugiohcollections.creator;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.odevpedro.yugiohcollections.creator",
        "com.odevpedro.yugiohcollections.shared"
})
public class CardCreatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CardCreatorApplication.class, args);
    }
}