package com.tunaforce.hub.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_hub_route")
public class HubRoute extends Timestamped{

    @Id
    @GeneratedValue
    @Column(name = "hub_route_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID hubRouteId;

    @Column(name = "origin_hub_id", nullable = false)
    private UUID originHubId;

    @Column(name = "destination_hub_id", nullable = false)
    private UUID destinationHubId;

    @Column(name = "transit_time", nullable = false)
    private Long transitTime;       // Second

    @Column(name = "distance", nullable = false)
    private Double distance;        // Kilometer

    @Column(name = "comment")
    private String comment;

    @Builder
    public HubRoute(UUID originHubId, UUID destinationHubId, Long transitTime, Double distance) {
        this.originHubId = originHubId;
        this.destinationHubId = destinationHubId;
        this.transitTime = transitTime;
        this.distance = distance;
    }

    public void update(Long transitTime, Double distance, String comment) {
        this.transitTime = transitTime;
        this.distance = distance;
        this.comment = comment;
    }

}
