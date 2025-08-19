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
import java.util.UUID;

@Slf4j
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
    @CacheEvict(value = "hub", allEntries = true)
    public HubUpdateResponseDto updateHub(UUID hubId, HubUpdateRequestDto requestDto) {
        Hub hub = readHub(hubId);   // 기존 허브 조회

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
    @CacheEvict(value = "hub", allEntries = true)
    public HubDeleteResponseDto deleteHub(UUID hubId) {
        Hub hub = readHub(hubId);   // 기존 허브 조회

        /*Feign 호출로 현재 사용자 ID 가져오기*/
        /*임시로 랜덤 UUID 넣어줌, 추후 수정 필요*/
        UUID userId = UUID.randomUUID();
        hub.delete(userId);
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
        if(page < 0 || size <= 0) {
            throw new ApplicationException(HubException.INVALID_PAGE_OR_SIZE);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        List<Hub> hubs = hubRepository.findAll(pageable);

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
}
