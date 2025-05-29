package com.example.ssafy_bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient bankWebClient() {
        return WebClient.builder()
                .baseUrl("https://finopenapi.ssafy.io/ssafy/api/v1")
                .build();
    }

}
