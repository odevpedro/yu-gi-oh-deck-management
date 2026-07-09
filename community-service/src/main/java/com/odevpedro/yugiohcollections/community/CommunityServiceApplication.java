package com.odevpedro.yugiohcollections.community;

import com.odevpedro.yugiohcollections.shared.config.CorrelationIdConfiguration;
import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.odevpedro.yugiohcollections.community",
        "com.odevpedro.yugiohcollections.shared"
})
@EnableScheduling
@EnableKafka
@EnableConfigurationProperties(JwtProperties.class)
@Import(CorrelationIdConfiguration.class)
@EnableFeignClients(basePackages = {
        "com.odevpedro.yugiohcollections.shared.security",
        "com.odevpedro.yugiohcollections.community.adapter.out.external"
})

public class CommunityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityServiceApplication.class, args);
    }
}
