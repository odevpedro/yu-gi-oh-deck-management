package com.odevpedro.yugiohcollections;

import com.odevpedro.yugiohcollections.shared.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients(basePackages = "com.odevpedro.yugiohcollections.card.adapter.out.external")
@EnableCaching
@EnableConfigurationProperties(JwtProperties.class)
public class YugiohCollectionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(YugiohCollectionsApplication.class, args);
	}

}
