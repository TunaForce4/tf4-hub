package com.tunaforce.hub.common.client.dto;

import java.util.UUID;

public record UserInfoResponseDto(
        UUID userId,
        String userLoginId,
        String username,
        String role,
        String slackId,
        String tel
) {
}
