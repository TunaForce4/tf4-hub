package com.tunaforce.hub.service;

import com.tunaforce.hub.entity.Hub;
import com.tunaforce.hub.entity.HubRoute;
import com.tunaforce.hub.repository.HubRepository;
import com.tunaforce.hub.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubRouteService {
    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository;
    private final NaverMapsService naverMapsService;

    @Transactional
    public void createHubRoutesAutomatically(Hub newHub){
        // newHub를 제외한 기존 허브 모두 조회
        List<Hub> hubList = hubRepository.findAllByHubIdNot(newHub.getHubId());

        // newHub를 출발지로 설정, 모든 허브와 연결
        for(Hub goalHub : hubList){
            createHubRoute(newHub, goalHub);
        }

        // newHub를 도착지로 설정, 모든 허브와 연결
        for(Hub startHub : hubList){
            createHubRoute(startHub, newHub);
        }
    }

    private void createHubRoute(Hub startHub, Hub goalHub){
        // Naver Maps API 호출하여 이동 거리와 시간 검색
        Map<String, Number> directions = naverMapsService.getDirections(startHub, goalHub);
        Double distance = directions.get("distance").doubleValue();
        Long transitTime = directions.get("transitTime").longValue();

        HubRoute hubRoute = HubRoute.builder()
                .originHubId(startHub.getHubId())
                .destinationHubId(goalHub.getHubId())
                .transitTime(transitTime)
                .distance(distance)
                .build();

        HubRoute savedHubRoute = hubRouteRepository.save(hubRoute);
    }
}
