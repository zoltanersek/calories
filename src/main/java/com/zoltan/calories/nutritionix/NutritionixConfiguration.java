package com.zoltan.calories.nutritionix;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class NutritionixConfiguration {

    @Value("${nutritionix.endpoint}")
    private String endpoint;

    @Value("${nutritionix.app.id}")
    private String appId;

    @Value("${nutritionix.app.key}")
    private String appKey;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        return templateBuilder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(2))
                .defaultHeader("x-app-id", appId)
                .defaultHeader("x-app-key", appKey)
                .defaultHeader("x-remote-user-id", "0")
                .rootUri(endpoint)
                .build();
    }

}
