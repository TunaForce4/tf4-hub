package com.tunaforce.hub.repository.impl;

import com.tunaforce.hub.entity.HubRoute;
import com.tunaforce.hub.repository.HubRouteRepository;
import com.tunaforce.hub.repository.jpa.JpaHubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubRouteRepositoryImpl implements HubRouteRepository {
    private final JpaHubRouteRepository jpaHubRouteRepository;


    @Override
    public HubRoute save(HubRoute hubRoute) {
        return jpaHubRouteRepository.save(hubRoute);
    }

    @Override
    public Optional<HubRoute> findByHubId(UUID originHubId, UUID destinationHubId) {
        return jpaHubRouteRepository.findByOriginHubIdAndDestinationHubIdAndDeletedAtIsNull(originHubId, destinationHubId);
    }

}
