package com.transitx.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bus_number", nullable = false, unique = true, length = 50)
    private String busNumber;

    @Column(nullable = false)
    private Integer capacity = 40;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BusStatus status = BusStatus.ACTIVE;

    @Column(name = "model_plate", length = 100)
    private String modelPlate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Bus() {}

    public Bus(String busNumber, Integer capacity, BusStatus status, String modelPlate) {
        this.busNumber = busNumber;
        this.capacity = capacity;
        this.status = status;
        this.modelPlate = modelPlate;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public BusStatus getStatus() { return status; }
    public void setStatus(BusStatus status) { this.status = status; }

    public String getModelPlate() { return modelPlate; }
    public void setModelPlate(String modelPlate) { this.modelPlate = modelPlate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
