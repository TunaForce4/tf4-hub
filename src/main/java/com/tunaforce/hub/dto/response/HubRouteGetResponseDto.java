package com.tunaforce.hub.dto.response;

import java.util.UUID;

public record HubRouteGetResponseDto(UUID HubRouteId,
                                     String originHubName,
                                     String destinationHubName,
                                     Long transitTime,
                                     Double distance) {
}
