package com.tunaforce.hub.repository.jpa;

import com.tunaforce.hub.entity.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaHubRouteRepository extends JpaRepository<HubRoute, UUID> {
    Optional<HubRoute> findByOriginHubIdAndDestinationHubIdAndDeletedAtIsNull(UUID originHubId, UUID destinationHubId);
}

