package com.tunaforce.hub.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tunaforce.hub.common.exception.ApplicationException;
import com.tunaforce.hub.common.exception.HubException;
import com.tunaforce.hub.entity.Hub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverMapsService {
    private final WebClient naverGeocodingClient;   // Geocoding API
    private final WebClient naverDirectionsClient;  // Directions5 API

    /* Naver Maps API로부터 위도, 경도 받아오는 메서드*/
    public Map<String, Double> getCoordinates(String address) {
        log.info("주소로 좌표 검색 시작: {}", address);

        JsonNode response = naverGeocodingClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", address).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        log.info("Geocoding API 응답: {}", response);

        if (response == null || response.get("addresses").isEmpty()) {
            log.error("유효하지 않은 주소: {}", address);
            throw new ApplicationException(HubException.INVALID_HUB_ADDRESS);
        }

        JsonNode addresses = response.get("addresses").get(0);
        double longitude = addresses.get("x").asDouble();
        double latitude = addresses.get("y").asDouble();

        log.info("좌표 변환 결과 - 위도: {}, 경도: {}", latitude, longitude);
        return Map.of("latitude", latitude, "longitude", longitude);
    }

    /* Naver Maps API로부터 최적 경로 정보를 받아오는 메서드*/
    public Map<String, Number> getDirections(Hub startHub, Hub goalHub) {
        // 좌표값 유효성 검증 추가
        if (startHub.getHubLatitude() == null || startHub.getHubLongitude() == null ||
                goalHub.getHubLatitude() == null || goalHub.getHubLongitude() == null) {
            log.error("허브 좌표 정보가 누락됨 - 출발지: lat={}, lng={}, 도착지: lat={}, lng={}",
                    startHub.getHubLatitude(), startHub.getHubLongitude(),
                    goalHub.getHubLatitude(), goalHub.getHubLongitude());
            throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
        }

        String start = startHub.getHubLongitude() + "," + startHub.getHubLatitude();
        String goal = goalHub.getHubLongitude() + "," + goalHub.getHubLatitude();

        log.info("경로 검색 시작 - 출발지: {} ({}), 도착지: {} ({})",
                startHub.getHubName(), start, goalHub.getHubName(), goal);

        try {
            JsonNode response = naverDirectionsClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/driving")
                            .queryParam("start", start)
                            .queryParam("goal", goal)
                            .queryParam("option", "trafast")
                            .queryParam("cartype", 2)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.info("Directions API 전체 응답: {}", response);

            // 응답 구조 상세 로깅
            if (response == null) {
                log.error("Directions API 응답이 null");
                throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
            }

            // 오류 코드 확인
            if (response.has("code") && response.get("code").asInt() != 0) {
                log.error("Directions API 오류 코드: {}, 메시지: {}",
                        response.get("code").asInt(),
                        response.has("message") ? response.get("message").asText() : "알 수 없는 오류");
                throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
            }

            JsonNode routeNode = response.get("route");
            if (routeNode == null) {
                log.error("route 노드가 존재하지 않음");
                throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
            }

            log.info("route 노드 내용: {}", routeNode);

            // traoptimal이 아닌 다른 경로 옵션도 확인
            String[] routeOptions = {"traoptimal", "trafast", "tracomfort"};
            JsonNode selectedRoute = null;
            String selectedOption = null;

            for (String option : routeOptions) {
                if (routeNode.has(option) && routeNode.get(option).isArray() && !routeNode.get(option).isEmpty()) {
                    selectedRoute = routeNode.get(option);
                    selectedOption = option;
                    log.info("경로 옵션 '{}' 사용 가능", option);
                    break;
                }
            }

            if (selectedRoute == null) {
                log.error("사용 가능한 경로 옵션이 없음. route 노드: {}", routeNode);
                throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
            }

            JsonNode firstRoute = selectedRoute.get(0);
            log.info("선택된 경로 ({}) 첫 번째 항목: {}", selectedOption, firstRoute);

            JsonNode summary = firstRoute.get("summary");
            if (summary == null) {
                log.error("summary 노드가 존재하지 않음. firstRoute: {}", firstRoute);
                throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
            }

            log.info("summary 노드 내용: {}", summary);

            if (!summary.has("distance") || !summary.has("duration")) {
                log.error("distance 또는 duration 필드가 없음. summary: {}", summary);
                throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
            }

            double distance = summary.get("distance").asDouble();
            int transitTime = summary.get("duration").asInt();

            log.info("경로 정보 - 거리: {}m, 소요시간: {}ms", distance, transitTime);

            // 단위 변환 (미터 -> 킬로미터, 밀리초 -> 초)
            double distanceInKm = distance / 1000.0;
            long transitTimeInSeconds = transitTime / 1000L;

            log.info("변환된 경로 정보 - 거리: {}km, 소요시간: {}초", distanceInKm, transitTimeInSeconds);

            return Map.of(
                    "distance", distanceInKm,
                    "transitTime", transitTimeInSeconds
            );

        } catch (Exception e) {
            log.error("Directions API 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new ApplicationException(HubException.INVALID_HUB_ROUTE);
        }
    }
}