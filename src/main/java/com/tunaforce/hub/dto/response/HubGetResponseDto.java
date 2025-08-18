package com.tunaforce.hub.dto.response;

import java.util.UUID;

public record HubGetResponseDto(UUID hubId,
                                String hubName,
                                String hubAddress,
                                Double hubLatitude,
                                Double hubLongitude) {
}
