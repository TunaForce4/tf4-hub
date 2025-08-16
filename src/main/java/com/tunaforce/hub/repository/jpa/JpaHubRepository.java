package com.tunaforce.hub.repository.jpa;

import com.tunaforce.hub.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaHubRepository extends JpaRepository<Hub, UUID> {
    boolean existsByHubName(String hubName);
}
