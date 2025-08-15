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
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID hubId;

    @Column(nullable = false)
    private String hubName;

    @Column(nullable = false)
    private String hubAddress;

    private Double hubLatitude;

    private Double hubLongitude;

    private Long hubAdminId;

    @Builder
    public Hub(String hubName, String hubAddress, Double hubLatitude, Double hubLongitude) {
        this.hubName = hubName;
        this.hubAddress = hubAddress;
        this.hubLatitude = hubLatitude;
        this.hubLongitude = hubLongitude;
    }

    public void setHubAdminId(Long hubAdminId) {
        this.hubAdminId = hubAdminId;
    }
}
