package com.transitx.dto;

public class DriverDTO {
    private Long id;
    private String driverCode;
    private String fullName;
    private String phone;
    private String licenseNo;
    private Integer experienceYears;
    private Long assignedBusId;
    private String assignedBusNumber;
    private String assignedRouteName;

    public DriverDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getAssignedBusId() { return assignedBusId; }
    public void setAssignedBusId(Long assignedBusId) { this.assignedBusId = assignedBusId; }

    public String getAssignedBusNumber() { return assignedBusNumber; }
    public void setAssignedBusNumber(String assignedBusNumber) { this.assignedBusNumber = assignedBusNumber; }

    public String getAssignedRouteName() { return assignedRouteName; }
    public void setAssignedRouteName(String assignedRouteName) { this.assignedRouteName = assignedRouteName; }
}
