package com.tunaforce.hub.common.repository;

import com.tunaforce.hub.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaHubRepository extends JpaRepository<Hub, UUID> {

}
