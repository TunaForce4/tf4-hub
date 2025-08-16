package com.tunaforce.hub.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_hub")
public class Hub {

    @Id
    @GeneratedValue
    @Column(name = "hub_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID hubId;

    @Column(name = "hub_name", nullable = false)
    private String hubName;

    @Column(name = "hub_address", nullable = false)
    private String hubAddress;

    @Column(name = "hub_latitude")
    private Double hubLatitude;

    @Column(name = "hub_longitude")
    private Double hubLongitude;

    @Column(name = "hub_admin_id")
    private UUID hubAdminId;

    @Builder
    public Hub(String hubName, String hubAddress, Double hubLatitude, Double hubLongitude) {
        this.hubName = hubName;
        this.hubAddress = hubAddress;
        this.hubLatitude = hubLatitude;
        this.hubLongitude = hubLongitude;
    }

    public void update(String hubName, String hubAddress, Double hubLatitude, Double hubLongitude) {
        this.hubName = hubName;
        this.hubAddress = hubAddress;
        this.hubLatitude = hubLatitude;
        this.hubLongitude = hubLongitude;
    }

    public void setHubAdminId(UUID hubAdminId) {
        this.hubAdminId = hubAdminId;
    }
}
