package com.tunaforce.hub.controller;

import com.tunaforce.hub.dto.request.HubRouteUpdateRequestDto;
import com.tunaforce.hub.dto.response.HubRouteGetResponseDto;
import com.tunaforce.hub.service.HubRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{hubRouteId}")
    public ResponseEntity<HubRouteGetResponseDto> updateOneHubRoute(@PathVariable UUID hubRouteId,
                                                                    @Validated @RequestBody HubRouteUpdateRequestDto requestDto){
        return new ResponseEntity<>(hubRouteService.updateOneHubRoute(hubRouteId, requestDto), HttpStatus.OK);
    }
}
