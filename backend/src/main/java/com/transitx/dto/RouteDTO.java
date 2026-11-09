package com.transitx.dto;

import java.math.BigDecimal;
import java.util.List;

public class RouteDTO {
    private Long id;
    private String routeName;
    private String routeCode;
    private String startPoint;
    private String endPoint;
    private String departureTime;
    private String arrivalTime;
    private String estDuration;
    private BigDecimal distanceKm;
    private Integer totalStops;
    private String assignedBusNumber;
    private String assignedDriverName;
    private List<StopDTO> stops;

    public RouteDTO() {}

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

    public Integer getTotalStops() { return totalStops; }
    public void setTotalStops(Integer totalStops) { this.totalStops = totalStops; }

    public String getAssignedBusNumber() { return assignedBusNumber; }
    public void setAssignedBusNumber(String assignedBusNumber) { this.assignedBusNumber = assignedBusNumber; }

    public String getAssignedDriverName() { return assignedDriverName; }
    public void setAssignedDriverName(String assignedDriverName) { this.assignedDriverName = assignedDriverName; }

    public List<StopDTO> getStops() { return stops; }
    public void setStops(List<StopDTO> stops) { this.stops = stops; }
}
