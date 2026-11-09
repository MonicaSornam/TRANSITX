package com.transitx.dto;

import java.math.BigDecimal;
import java.util.List;

public class StopDTO {
    private Long id;
    private String stopName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String landmark;
    private Integer sequence;
    private String pickupTime;

    public StopDTO() {}

    public StopDTO(Long id, String stopName, BigDecimal latitude, BigDecimal longitude, String landmark, Integer sequence, String pickupTime) {
        this.id = id;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.landmark = landmark;
        this.sequence = sequence;
        this.pickupTime = pickupTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStopName() { return stopName; }
    public void setStopName(String stopName) { this.stopName = stopName; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }

    public String getPickupTime() { return pickupTime; }
    public void setPickupTime(String pickupTime) { this.pickupTime = pickupTime; }
}
