package com.tunaforce.hub.repository;

import com.tunaforce.hub.entity.Hub;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository{
    Hub save(Hub hub);
    Optional<Hub> findById(UUID hubId);
    boolean existsByHubName(String hubName);
    List<Hub> findAll(Pageable pageable);
    List<Hub> findAllByHubIdNot(UUID hubId);
    List<Hub> findAll(Sort sort);
    Optional<Hub> findByHubAdminId(UUID hubAdminId);
}
