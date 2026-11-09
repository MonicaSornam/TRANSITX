package com.transitx.dto;

public class BusDTO {
    private Long id;
    private String busNumber;
    private Integer capacity;
    private String status;
    private String modelPlate;
    private String currentRouteName;
    private String currentDriverName;

    public BusDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getModelPlate() { return modelPlate; }
    public void setModelPlate(String modelPlate) { this.modelPlate = modelPlate; }

    public String getCurrentRouteName() { return currentRouteName; }
    public void setCurrentRouteName(String currentRouteName) { this.currentRouteName = currentRouteName; }

    public String getCurrentDriverName() { return currentDriverName; }
    public void setCurrentDriverName(String currentDriverName) { this.currentDriverName = currentDriverName; }
}
