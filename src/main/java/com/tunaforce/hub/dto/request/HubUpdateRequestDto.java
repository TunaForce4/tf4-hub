package com.tunaforce.hub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HubUpdateRequestDto(
        @NotBlank(message = "허브 이름은 필수 입력값입니다.") String hubName,
        @NotBlank(message = "허브 주소는 필수 입력값입니다.") String hubAddress,
        @NotNull(message = "변경 사유를 작성해주세요.") String comment) {
}
