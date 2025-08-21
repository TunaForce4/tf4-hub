package com.tunaforce.hub.common.client;

import com.tunaforce.hub.common.client.dto.UserInfoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth", path = "/users")
public interface AuthClient {

    @GetMapping("/{userId}")
    UserInfoResponseDto getUserInfo(@PathVariable("userId") UUID userId);
}
