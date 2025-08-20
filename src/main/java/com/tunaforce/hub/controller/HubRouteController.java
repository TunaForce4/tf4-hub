package com.tunaforce.hub.controller;

import com.tunaforce.hub.dto.response.HubRouteGetResponseDto;
import com.tunaforce.hub.service.HubRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/hub-routes")
@RequiredArgsConstructor
public class HubRouteController {
    private final HubRouteService hubRouteService;

    @GetMapping
    public ResponseEntity<HubRouteGetResponseDto> getHubRoute(@RequestParam UUID from,
                                                              @RequestParam UUID to){
        return new ResponseEntity<>(hubRouteService.getHubRoute(from, to), HttpStatus.OK);
    }
}
