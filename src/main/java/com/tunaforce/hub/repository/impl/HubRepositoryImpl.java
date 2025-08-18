package com.tunaforce.hub.repository.impl;

import com.tunaforce.hub.entity.Hub;
import com.tunaforce.hub.repository.HubRepository;
import com.tunaforce.hub.repository.jpa.JpaHubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {

    private final JpaHubRepository jpaHubRepository;

    @Override
    public Hub save(Hub hub) {
        return jpaHubRepository.save(hub);
    }

    @Override
    public Optional<Hub> findById(UUID hubId) {
        return jpaHubRepository.findByHubIdAndDeletedAtIsNull(hubId);
    }

    @Override
    public boolean existsByHubName(String hubName) {
        return jpaHubRepository.existsByHubNameAndDeletedAtIsNull(hubName);
    }

    @Override
    public List<Hub> findAll(Pageable pageable) {
        return jpaHubRepository.findAllByDeletedAtIsNull(pageable);
    }

}
