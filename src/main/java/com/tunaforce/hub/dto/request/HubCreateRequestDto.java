package com.tunaforce.hub.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HubCreateRequestDto(
        @NotBlank(message = "허브 이름과 주소는 필수 입력값입니다.") String hubName,
        @NotBlank(message = "허브 이름과 주소는 필수 입력값입니다.") String hubAddress) {
}
