package com.tunaforce.hub.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverMapsService {
    private final WebClient webClient;

    /*주소를 매개변수로 받아서 위도를 반환하는 메서드*/
    public Double getLatitude(String address) {
        Map<String, Double> coordinates = getCoordinates(address);
        return coordinates.get("latitude");
    }

    /*주소를 매개변수로 받아서 경도를 반환하는 메서드*/
    public Double getLongitude(String address) {
        Map<String, Double> coordinates = getCoordinates(address);
        return coordinates.get("longitude");
    }

    /* Naver Maps API로부터 위도, 경도 받아오는 메서드*/
    private Map<String, Double> getCoordinates(String address) {
        JsonNode response = webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", address).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || response.get("addresses").isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 주소입니다. ");
        }

        JsonNode addresses = response.get("addresses").get(0);
        double longitude = addresses.get("x").asDouble();
        double latitude = addresses.get("y").asDouble();

        return Map.of("latitude", latitude, "longitude", longitude);
    }
}
