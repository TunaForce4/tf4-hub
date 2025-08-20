package com.tunaforce.hub.dto.request;

import jakarta.validation.constraints.NotNull;

public record HubRouteUpdateRequestDto(
        @NotNull(message = "경로 업데이트 사유를 작성해주세요.") String comment
) {
}
