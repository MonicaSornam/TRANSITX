package com.transitx.dto;

public class StudentDTO {
    private Long id;
    private String regNo;
    private String department;
    private String yearOfStudy;
    private String fullName;
    private String email;
    private String phone;
    private Long assignedBusId;
    private String assignedBusNumber;
    private Long assignedRouteId;
    private String assignedRouteName;

    public StudentDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Long getAssignedBusId() { return assignedBusId; }
    public void setAssignedBusId(Long assignedBusId) { this.assignedBusId = assignedBusId; }

    public String getAssignedBusNumber() { return assignedBusNumber; }
    public void setAssignedBusNumber(String assignedBusNumber) { this.assignedBusNumber = assignedBusNumber; }

    public Long getAssignedRouteId() { return assignedRouteId; }
    public void setAssignedRouteId(Long assignedRouteId) { this.assignedRouteId = assignedRouteId; }

    public String getAssignedRouteName() { return assignedRouteName; }
    public void setAssignedRouteName(String assignedRouteName) { this.assignedRouteName = assignedRouteName; }
}
