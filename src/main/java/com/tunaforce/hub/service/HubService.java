package com.tunaforce.hub.service;

import com.tunaforce.hub.common.exception.ApplicationException;
import com.tunaforce.hub.common.exception.HubException;
import com.tunaforce.hub.dto.request.HubCreateRequestDto;
import com.tunaforce.hub.dto.request.HubUpdateRequestDto;
import com.tunaforce.hub.dto.response.HubCreateResponseDto;
import com.tunaforce.hub.dto.response.HubDeleteResponseDto;
import com.tunaforce.hub.dto.response.HubGetResponseDto;
import com.tunaforce.hub.dto.response.HubUpdateResponseDto;
import com.tunaforce.hub.entity.Hub;
import com.tunaforce.hub.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubService {
    private final HubRepository hubRepository;
    private final NaverMapsService naverMapsService;
    private final HubRouteService hubRouteService;

    @Transactional
    public HubCreateResponseDto createHub(String roles, HubCreateRequestDto requestDto) {
        // 권한 확인
        validateMasterRole(roles);

        //허브 이름 중복 검증
        checkDuplicateHubName(requestDto.hubName());

        // 위도, 경도는 네이버 지도 API 호출로 설정
        Map<String, Double> coordinates = naverMapsService.getCoordinates(requestDto.hubAddress());
        Double latitude =   coordinates.get("latitude");
        Double longitude =   coordinates.get("longitude");

        Hub hub = Hub.builder()
                .hubName(requestDto.hubName())
                .hubAddress(requestDto.hubAddress())
                .hubLatitude(latitude)
                .hubLongitude(longitude)
                .build();

        Hub savedHub = hubRepository.save(hub);

        // 허브 경로 서비스 호출, 허브 간 이동 경로 자동 생성
        hubRouteService.createHubRoutesAutomatically(savedHub);

        return new HubCreateResponseDto(savedHub.getHubId());
    }

    @Transactional
    @CacheEvict(value = "hub", allEntries = true)
    public HubUpdateResponseDto updateHub(String roles, UUID hubId, HubUpdateRequestDto requestDto) {
        // 권한 확인
        validateMasterRole(roles);

        Hub hub = readHub(hubId);   // 기존 허브 조회

        // 변경 사항이 없는 경우 BAD REQUEST 응답
        if(hub.getHubName().equals(requestDto.hubName()) && hub.getHubAddress().equals(requestDto.hubAddress())) {
            throw new ApplicationException(HubException.HUB_NO_CHANGE);
        }

        // 이름이 기존 이름과 다를 때만 중복 검증
        if(!hub.getHubName().equals(requestDto.hubName())) {
            checkDuplicateHubName(requestDto.hubName());
        }

        // 위도, 경도는 네이버 지도 API 호출로 설정
        Map<String, Double> coordinates = naverMapsService.getCoordinates(requestDto.hubAddress());
        Double latitude =   coordinates.get("latitude");
        Double longitude =   coordinates.get("longitude");

        // 허브 업데이트
        hub.update(requestDto.hubName(),
                requestDto.hubAddress(),
                latitude,
                longitude,
                requestDto.comment());

        // 허브 경로 서비스 호출, 허브 간 이동 경로 자동 업데이트
        hubRouteService.updateHubRoutesAutomatically(hub);

        return new HubUpdateResponseDto(hub.getHubId());

    }

    @Transactional
    @CacheEvict(value = "hub", allEntries = true)
    public HubDeleteResponseDto deleteHub(String userId, String roles, UUID hubId) {
        // 권한 확인
        validateMasterRole(roles);

        Hub hub = readHub(hubId);   // 기존 허브 조회

        // 현재 사용자 ID 넣어줌
        UUID deletedAt = UUID.fromString(userId);
        hub.delete(deletedAt);

        // 허브 경로 서비스 호출, 허브 간 이동 경로 자동 삭제
        hubRouteService.deleteHubRoutesAutomatically(hub, UUID.fromString(userId));

        return new HubDeleteResponseDto(true);
    }

    @Cacheable(value = "hub", key = "#hubId")
    public HubGetResponseDto getHub(UUID hubId) {
        log.info("캐시되지 않은 허브 조회: {}", hubId);

        Hub hub = readHub(hubId);   // 기존 허브 조회

        return new HubGetResponseDto(hub.getHubId(), hub.getHubName(), hub.getHubAddress(),
                hub.getHubLatitude(), hub.getHubLongitude());
    }

    public List<HubGetResponseDto> getHubList(int page, int size) {
        if(page < 0 || size < 0) {
            throw new ApplicationException(HubException.INVALID_PAGE_OR_SIZE);
        }

        List<Hub> hubs;
        if(size == 0){
            hubs = hubRepository.findAll(Sort.by("createdAt").ascending());
        } else{
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
            hubs = hubRepository.findAll(pageable);
        }
        return hubs.stream()
                .map(hub -> new HubGetResponseDto(
                        hub.getHubId(),
                        hub.getHubName(),
                        hub.getHubAddress(),
                        hub.getHubLatitude(),
                        hub.getHubLongitude()))
                .toList();
    }

    /* 허브 이름 중복 확인 메서드 */
    private void checkDuplicateHubName(String hubName) {
        if(hubRepository.existsByHubName(hubName)){
            throw new ApplicationException(HubException.HUB_NAME_DUPLICATE);
        }
    }

    /*ID로 허브 조회 메서드*/
    private Hub readHub(UUID hubId) {
        return hubRepository.findById(hubId)
                .orElseThrow(() -> new ApplicationException(HubException.HUB_NOT_FOUND));
    }

    /* 권한 확인 메서드 (Master 인지 확인)*/
    private void validateMasterRole(String roles) {
        if (roles == null || !roles.contains("Master")) {
            throw new ApplicationException(HubException.ACCESS_DENIED);
        }
    }
}
