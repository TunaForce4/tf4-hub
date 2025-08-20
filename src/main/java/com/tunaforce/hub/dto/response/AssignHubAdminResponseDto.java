package com.tunaforce.hub.dto.response;

import java.util.UUID;

public record AssignHubAdminResponseDto(UUID hubId,
                                        String hubName,
                                        UUID hubAdminId) {
}
