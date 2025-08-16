package com.tunaforce.hub.service;

import com.tunaforce.hub.dto.request.HubCreateRequestDto;
import com.tunaforce.hub.dto.response.HubCreateResponseDto;
import com.tunaforce.hub.entity.Hub;
import com.tunaforce.hub.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubService {
    private final HubRepository hubRepository;
    private final NaverMapsService naverMapsService;

    @Transactional
    public HubCreateResponseDto createHub(HubCreateRequestDto requestDto) {
        checkDuplicateHubName(requestDto.hubName());    //허브 이름 중복 검증

        /* 위도, 경도는 외부 API 호출로 설정, 추후 수정 필요 */
        Hub hub = Hub.builder()
                .hubName(requestDto.hubName())
                .hubAddress(requestDto.hubAddress())
                .hubLatitude(naverMapsService.getLatitude(requestDto.hubAddress()))
                .hubLongitude(naverMapsService.getLongitude(requestDto.hubAddress()))
                .build();

        Hub savedHub = hubRepository.save(hub);

        return new HubCreateResponseDto(savedHub.getHubId());
    }

    /* 허브 이름 중복 확인 메서드 */
    private void checkDuplicateHubName(String hubName) {
        if(hubRepository.existsByHubName(hubName)){
            throw new IllegalArgumentException("이미 동일한 허브명이 존재합니다.");
        }
    }
}
