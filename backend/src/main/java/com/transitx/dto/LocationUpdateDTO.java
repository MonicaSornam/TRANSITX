package com.transitx.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LocationUpdateDTO {
    private Long busId;
    private String busNumber;
    private String routeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal speed;
    private String status;
    private LocalDateTime lastUpdated;
    private Boolean isSimulated = true;
    private String estimatedArrival;
    private String nextStopName;

    public LocationUpdateDTO() {}

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public BigDecimal getSpeed() { return speed; }
    public void setSpeed(BigDecimal speed) { this.speed = speed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Boolean getIsSimulated() { return isSimulated; }
    public void setIsSimulated(Boolean isSimulated) { this.isSimulated = isSimulated; }

    public String getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(String estimatedArrival) { this.estimatedArrival = estimatedArrival; }

    public String getNextStopName() { return nextStopName; }
    public void setNextStopName(String nextStopName) { this.nextStopName = nextStopName; }
}
