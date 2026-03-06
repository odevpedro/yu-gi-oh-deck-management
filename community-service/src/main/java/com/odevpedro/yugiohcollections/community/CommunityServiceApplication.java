package com.odevpedro.yugiohcollections.community;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.odevpedro.yugiohcollections.community",
        "com.odevpedro.yugiohcollections.shared"
})
@EnableScheduling
@EnableConfigurationProperties(JwtProperties.class)

public class CommunityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityServiceApplication.class, args);
    }
}
