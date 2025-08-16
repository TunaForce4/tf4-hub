package com.tunaforce.hub.repository;

import com.tunaforce.hub.entity.Hub;

public interface HubRepository{
    Hub save(Hub hub);
    boolean existsByHubName(String hubName);
}
