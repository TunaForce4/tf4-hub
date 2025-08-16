package com.tunaforce.hub.repository.impl;

import com.tunaforce.hub.entity.Hub;
import com.tunaforce.hub.repository.HubRepository;
import com.tunaforce.hub.repository.jpa.JpaHubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {

    private final JpaHubRepository jpaHubRepository;

    @Override
    public Hub save(Hub hub) {
        return jpaHubRepository.save(hub);
    }

    @Override
    public boolean existsByHubName(String hubName) {
        return jpaHubRepository.existsByHubName(hubName);
    }

}
