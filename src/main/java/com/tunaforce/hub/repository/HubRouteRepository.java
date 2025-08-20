package com.tunaforce.hub.repository;

import com.tunaforce.hub.entity.HubRoute;

import java.util.Optional;
import java.util.UUID;

public interface HubRouteRepository {
    HubRoute save(HubRoute hubRoute);
    Optional<HubRoute> findByHubId(UUID originHubId,  UUID destinationHubId);
    Optional<HubRoute> findByHubRouteId(UUID hubRouteId);
}
