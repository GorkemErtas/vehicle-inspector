package com.gorkem.vehicle_inspector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient aiRestClient(
            @Value("${application.ai-service.base-url}")
            String aiServiceBaseUrl
    ) {
        return RestClient.builder()
                .baseUrl(aiServiceBaseUrl)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}