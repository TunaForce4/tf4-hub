package com.tunaforce.hub.controller;

import com.tunaforce.hub.dto.request.HubCreateRequestDto;
import com.tunaforce.hub.dto.request.HubUpdateRequestDto;
import com.tunaforce.hub.dto.response.*;
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
    public ResponseEntity<HubCreateResponseDto> createHub(@RequestHeader("X-Roles") String roles,
                                                          @Validated @RequestBody HubCreateRequestDto requestDto) {
        return new ResponseEntity<>(hubService.createHub(roles, requestDto), HttpStatus.CREATED);
    }

    /*허브 수정 API*/
    @PutMapping("/{hubId}")
    public ResponseEntity<HubUpdateResponseDto> updateHub(@RequestHeader("X-Roles") String roles,
                                                          @PathVariable UUID hubId,
                                                          @Validated @RequestBody HubUpdateRequestDto requestDto) {
        return new ResponseEntity<>(hubService.updateHub(roles, hubId, requestDto), HttpStatus.OK);
    }

    /*허브 삭제 API*/
    @DeleteMapping("/{hubId}")
    public ResponseEntity<HubDeleteResponseDto> deleteHub(@RequestHeader("X-User-Id") String userId,
                                                          @RequestHeader("X-Roles") String roles,
                                                          @PathVariable UUID hubId) {
        return new ResponseEntity<>(hubService.deleteHub(userId, roles, hubId), HttpStatus.OK);
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

    /*허브 관리자 등록 API*/
    @PostMapping("/{hubId}/admins/{hubAdminId}")
    public ResponseEntity<AssignHubAdminResponseDto> assignHubAdmin(@RequestHeader("X-Roles") String roles,
                                                              @PathVariable UUID hubId,
                                                              @PathVariable UUID hubAdminId) {
        return new ResponseEntity<>(hubService.assignHubAdmin(roles, hubId, hubAdminId), HttpStatus.OK);
    }

    /*허브 관리자 조회 API*/
    @GetMapping("/admins/{hubAdminId}")
    public ResponseEntity<GetHubByAdminResponseDto> getHubByAdminId(@PathVariable UUID hubAdminId) {
        return new ResponseEntity<>(hubService.getHubByAdminId(hubAdminId), HttpStatus.OK);
    }

}