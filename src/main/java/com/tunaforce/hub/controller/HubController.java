package com.tunaforce.hub.controller;

import com.tunaforce.hub.dto.request.HubCreateRequestDto;
import com.tunaforce.hub.dto.request.HubUpdateRequestDto;
import com.tunaforce.hub.dto.response.HubCreateResponseDto;
import com.tunaforce.hub.dto.response.HubDeleteResponseDto;
import com.tunaforce.hub.dto.response.HubGetResponseDto;
import com.tunaforce.hub.dto.response.HubUpdateResponseDto;
import com.tunaforce.hub.service.HubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hubs")
@RequiredArgsConstructor
public class HubController {
    private final HubService hubService;

    /*허브 생성 API*/
    @PostMapping
    public ResponseEntity<HubCreateResponseDto> createHub(@Validated @RequestBody HubCreateRequestDto requestDto) {
        return new ResponseEntity<>(hubService.createHub(requestDto), HttpStatus.CREATED);
    }

    /*허브 수정 API*/
    @PutMapping("/{hubId}")
    public ResponseEntity<HubUpdateResponseDto> updateHub(@PathVariable UUID hubId,
                                                          @Validated @RequestBody HubUpdateRequestDto requestDto) {
        return new ResponseEntity<>(hubService.updateHub(hubId, requestDto), HttpStatus.OK);
    }

    /*허브 삭제 API*/
    @DeleteMapping("/{hubId}")
    public ResponseEntity<HubDeleteResponseDto> deleteHub(@PathVariable UUID hubId) {
        return new ResponseEntity<>(hubService.deleteHub(hubId), HttpStatus.OK);
    }

    /*허브 단건 조회 API*/
    @GetMapping("/{hubId}")
    public ResponseEntity<HubGetResponseDto> getHub(@PathVariable UUID hubId) {
        return new ResponseEntity<>(hubService.getHub(hubId), HttpStatus.OK);
    }

    /*허브 목록 조회 API*/
    @GetMapping
    public ResponseEntity<List<HubGetResponseDto>> getHubList(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(hubService.getHubList(page, size), HttpStatus.OK);
    }

}