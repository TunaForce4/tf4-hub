package com.tunaforce.hub.controller;

import com.tunaforce.hub.dto.request.HubCreateRequestDto;
import com.tunaforce.hub.dto.response.HubCreateResponseDto;
import com.tunaforce.hub.service.HubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hubs")
@RequiredArgsConstructor
public class HubController {
    private final HubService hubService;

    /*허브 생성 API*/
    @PostMapping
    public ResponseEntity<HubCreateResponseDto> createHub(@RequestBody HubCreateRequestDto hubCreateRequestDto) {
        return new ResponseEntity<>(hubService.createHub(hubCreateRequestDto), HttpStatus.CREATED);
    }
}
