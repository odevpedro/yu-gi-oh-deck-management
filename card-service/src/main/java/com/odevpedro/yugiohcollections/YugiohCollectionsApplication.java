package com.odevpedro.yugiohcollections;

import com.odevpedro.yugiohcollections.shared.config.CorrelationIdConfiguration;
import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@EnableFeignClients(basePackages = {"com.odevpedro.yugiohcollections.card.adapter.out.external", "com.odevpedro.yugiohcollections.shared.security"})
@EnableCaching
@EnableConfigurationProperties(JwtProperties.class)
@Import(CorrelationIdConfiguration.class)
@ComponentScan(basePackages = {
        "com.odevpedro.yugiohcollections.card",
        "com.odevpedro.yugiohcollections.shared"
})
public class YugiohCollectionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(YugiohCollectionsApplication.class, args);
	}

}
