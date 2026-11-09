package com.transitx.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_name", nullable = false, length = 100)
    private String routeName;

    @Column(name = "route_code", nullable = false, unique = true, length = 50)
    private String routeCode;

    @Column(name = "start_point", nullable = false, length = 100)
    private String startPoint;

    @Column(name = "end_point", nullable = false, length = 100)
    private String endPoint;

    @Column(name = "departure_time", nullable = false, length = 20)
    private String departureTime;

    @Column(name = "arrival_time", nullable = false, length = 20)
    private String arrivalTime;

    @Column(name = "est_duration", nullable = false, length = 50)
    private String estDuration;

    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Route() {}

    public Route(String routeName, String routeCode, String startPoint, String endPoint,
                 String departureTime, String arrivalTime, String estDuration, BigDecimal distanceKm) {
        this.routeName = routeName;
        this.routeCode = routeCode;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.estDuration = estDuration;
        this.distanceKm = distanceKm;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getStartPoint() { return startPoint; }
    public void setStartPoint(String startPoint) { this.startPoint = startPoint; }

    public String getEndPoint() { return endPoint; }
    public void setEndPoint(String endPoint) { this.endPoint = endPoint; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getEstDuration() { return estDuration; }
    public void setEstDuration(String estDuration) { this.estDuration = estDuration; }

    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
