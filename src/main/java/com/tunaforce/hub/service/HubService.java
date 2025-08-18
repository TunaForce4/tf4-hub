package com.tunaforce.hub.service;

import com.tunaforce.hub.common.exception.ApplicationException;
import com.tunaforce.hub.common.exception.HubException;
import com.tunaforce.hub.dto.request.HubCreateRequestDto;
import com.tunaforce.hub.dto.request.HubUpdateRequestDto;
import com.tunaforce.hub.dto.response.HubCreateResponseDto;
import com.tunaforce.hub.dto.response.HubDeleteResponseDto;
import com.tunaforce.hub.dto.response.HubUpdateResponseDto;
import com.tunaforce.hub.entity.Hub;
import com.tunaforce.hub.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubService {
    private final HubRepository hubRepository;
    private final NaverMapsService naverMapsService;

    @Transactional
    public HubCreateResponseDto createHub(HubCreateRequestDto requestDto) {
        //허브 이름 중복 검증
        checkDuplicateHubName(requestDto.hubName());

        // 위도, 경도는 외부 API 호출로 설정
        Hub hub = Hub.builder()
                .hubName(requestDto.hubName())
                .hubAddress(requestDto.hubAddress())
                .hubLatitude(naverMapsService.getLatitude(requestDto.hubAddress()))
                .hubLongitude(naverMapsService.getLongitude(requestDto.hubAddress()))
                .build();

        Hub savedHub = hubRepository.save(hub);
        return new HubCreateResponseDto(savedHub.getHubId());
    }

    @Transactional
    public HubUpdateResponseDto updateHub(UUID hubId, HubUpdateRequestDto requestDto) {
        // 기존 허브 조회
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new ApplicationException(HubException.HUB_NOT_FOUND));

        // 변경 사항이 없는 경우 BAD REQUEST 응답
        if(hub.getHubName().equals(requestDto.hubName()) && hub.getHubAddress().equals(requestDto.hubAddress())) {
            throw new ApplicationException(HubException.HUB_NO_CHANGE);
        }

        // 이름이 기존 이름과 다를 때만 중복 검증
        if(!hub.getHubName().equals(requestDto.hubName())) {
            checkDuplicateHubName(requestDto.hubName());
        }

        // 허브 업데이트
        hub.update(requestDto.hubName(),
                requestDto.hubAddress(),
                naverMapsService.getLatitude(requestDto.hubAddress()),
                naverMapsService.getLongitude(requestDto.hubAddress()),
                requestDto.comment());

        return new HubUpdateResponseDto(hub.getHubId());

    }

    @Transactional
    public HubDeleteResponseDto deleteHub(UUID hubId) {
        // 기존 허브 조회
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new ApplicationException(HubException.HUB_NOT_FOUND));

        /*Feign 호출로 현재 사용자 ID 가져오기*/
        /*임시로 랜덤 UUID 넣어줌, 추후 수정 필요*/
        UUID userId = UUID.randomUUID();
        hub.delete(userId);
        return new HubDeleteResponseDto(true);
    }

    /* 허브 이름 중복 확인 메서드 */
    private void checkDuplicateHubName(String hubName) {
        if(hubRepository.existsByHubName(hubName)){
            throw new ApplicationException(HubException.HUB_NAME_DUPLICATE);
        }
    }
}
