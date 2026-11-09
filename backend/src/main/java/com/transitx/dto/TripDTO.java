package com.transitx.dto;

import java.time.LocalDateTime;

public class TripDTO {
    private Long id;
    private Long busId;
    private String busNumber;
    private Long driverId;
    private String driverName;
    private Long routeId;
    private String routeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String tripStatus;
    private String currentStopName;

    public TripDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getTripStatus() { return tripStatus; }
    public void setTripStatus(String tripStatus) { this.tripStatus = tripStatus; }

    public String getCurrentStopName() { return currentStopName; }
    public void setCurrentStopName(String currentStopName) { this.currentStopName = currentStopName; }
}
