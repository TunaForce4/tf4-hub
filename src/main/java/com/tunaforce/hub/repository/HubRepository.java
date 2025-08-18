package com.tunaforce.hub.repository;

import com.tunaforce.hub.entity.Hub;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository{
    Hub save(Hub hub);
    Optional<Hub> findById(UUID hubId);
    boolean existsByHubName(String hubName);
}
