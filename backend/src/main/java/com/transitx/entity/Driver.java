package com.transitx.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "driver_code", nullable = false, unique = true, length = 50)
    private String driverCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "license_no", nullable = false, length = 50)
    private String licenseNo;

    @Column(name = "experience_years")
    private Integer experienceYears = 5;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_bus_id")
    private Bus assignedBus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Driver() {}

    public Driver(User user, String driverCode, String fullName, String phone, String licenseNo,
                  Integer experienceYears, Bus assignedBus) {
        this.user = user;
        this.driverCode = driverCode;
        this.fullName = fullName;
        this.phone = phone;
        this.licenseNo = licenseNo;
        this.experienceYears = experienceYears;
        this.assignedBus = assignedBus;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDriverCode() { return driverCode; }
    public void setDriverCode(String driverCode) { this.driverCode = driverCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public Bus getAssignedBus() { return assignedBus; }
    public void setAssignedBus(Bus assignedBus) { this.assignedBus = assignedBus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
