package com.tunaforce.hub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NaverMapsConfig {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    // Geocoding API (주소 기반 위도, 경도 검색)
    @Bean
    public WebClient naverGeocodingClient() {
        return WebClient.builder()
                .baseUrl("https://maps.apigw.ntruss.com/map-geocode/v2/geocode")
                .defaultHeader("x-ncp-apigw-api-key-id", clientId)
                .defaultHeader("x-ncp-apigw-api-key", clientSecret)
                .build();
    }

    // Directions5 API (최적 경로 검색)
    @Bean
    public WebClient naverDirectionsClient() {
        return WebClient.builder()
                .baseUrl("https://maps.apigw.ntruss.com/map-direction/v1")
                .defaultHeader("x-ncp-apigw-api-key-id", clientId)
                .defaultHeader("x-ncp-apigw-api-key", clientSecret)
                .build();
    }
}
