package com.tunaforce.hub.repository.jpa;

import com.tunaforce.hub.entity.Hub;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaHubRepository extends JpaRepository<Hub, UUID> {
    boolean existsByHubNameAndDeletedAtIsNull(String hubName);
    Optional<Hub> findByHubIdAndDeletedAtIsNull(UUID hubId);
    List<Hub> findAllByDeletedAtIsNull(Pageable pageable);
    List<Hub> findAllByHubIdNot(UUID hubId);
}
