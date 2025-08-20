package com.tunaforce.hub.dto.response;

import java.util.UUID;

public record GetHubByAdminResponseDto(UUID hubId, String hubName) {
}
