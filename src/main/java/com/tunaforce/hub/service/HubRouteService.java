package com.tunaforce.hub.service;

import com.tunaforce.hub.common.exception.ApplicationException;
import com.tunaforce.hub.common.exception.HubException;
import com.tunaforce.hub.dto.response.HubRouteGetResponseDto;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubRouteService {
    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository;
    private final NaverMapsService naverMapsService;

    @Transactional
    public HubRouteGetResponseDto getHubRoute(UUID from, UUID to) {
        HubRoute hubRoute = hubRouteRepository.findByHubId(from, to)
                .orElseThrow(()-> new ApplicationException(HubException.HUB_ROUTE_NOT_FOUND));

        String originHubName = hubRepository.findById(hubRoute.getOriginHubId())
                .orElseThrow(()-> new ApplicationException(HubException.HUB_NOT_FOUND)).getHubName();
        String destinationHubName = hubRepository.findById(hubRoute.getDestinationHubId())
                .orElseThrow(()-> new ApplicationException(HubException.HUB_NOT_FOUND)).getHubName();

        return new HubRouteGetResponseDto(hubRoute.getHubRouteId(),
                originHubName, destinationHubName, hubRoute.getTransitTime(), hubRoute.getDistance());
    }

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

    @Transactional
    public void updateHubRoutesAutomatically(Hub newHub){
        // newHub를 제외한 기존 허브 모두 조회
        List<Hub> hubList = hubRepository.findAllByHubIdNot(newHub.getHubId());

        // newHub를 출발지로 설정, 모든 허브와의 경로 업데이트
        for(Hub goalHub : hubList){
            updateHubRoute(newHub, goalHub);
        }

        // newHub를 도착지로 설정, 모든 허브와의 경로 업데이트
        for(Hub startHub : hubList){
            updateHubRoute(startHub, newHub);
        }
    }

    @Transactional
    public void deleteHubRoutesAutomatically(Hub newHub){
        // newHub를 제외한 기존 허브 모두 조회
        List<Hub> hubList = hubRepository.findAllByHubIdNot(newHub.getHubId());

        // newHub를 출발지로 설정, 모든 허브와의 경로 삭제
        for(Hub goalHub : hubList){
            deleteHubRoute(newHub, goalHub);
        }

        // newHub를 도착지로 설정, 모든 허브와의 경로 삭제
        for(Hub startHub : hubList){
            deleteHubRoute(startHub, newHub);
        }
    }

    /*출발허브, 도착허브 인스턴스를 매개변수로 받아서 허브 경로 생성 */
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

        hubRouteRepository.save(hubRoute);
    }

    /*출발허브, 도착허브 인스턴스를 매개변수로 받아서 허브 경로 업데이트 */
    private void updateHubRoute(Hub startHub, Hub goalHub){
        // Naver Maps API 호출하여 이동 거리와 시간 검색
        Map<String, Number> directions = naverMapsService.getDirections(startHub, goalHub);
        Double distance = directions.get("distance").doubleValue();
        Long transitTime = directions.get("transitTime").longValue();

        HubRoute hubRoute = hubRouteRepository.findByHubId(startHub.getHubId(), goalHub.getHubId())
                .orElseThrow(()-> new ApplicationException(HubException.HUB_ROUTE_NOT_FOUND));

        //허브 경로 업데이트
        hubRoute.update(transitTime, distance, "허브 정보 변경");
    }

    /*출발허브, 도착허브 인스턴스를 매개변수로 받아서 허브 경로 자동 삭제*/
    private void deleteHubRoute(Hub startHub, Hub goalHub){
        HubRoute hubRoute = hubRouteRepository.findByHubId(startHub.getHubId(), goalHub.getHubId())
                .orElseThrow(()-> new ApplicationException(HubException.HUB_ROUTE_NOT_FOUND));

        // 추후 수정 필요
        UUID userId = UUID.randomUUID();
        hubRoute.delete(userId);
    }
}
