package com.transitx.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BusPassDTO {
    private Long id;
    private String passCode;
    private Long studentId;
    private String studentName;
    private String regNo;
    private String department;
    private Long routeId;
    private String routeName;
    private String routeCode;
    private Long busId;
    private String busNumber;
    private Long boardingStopId;
    private String boardingStopName;
    private String status;
    private LocalDate validityStart;
    private LocalDate validityEnd;
    private String qrCodeData;
    private String rejectionReason;
    private LocalDateTime createdAt;

    public BusPassDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPassCode() { return passCode; }
    public void setPassCode(String passCode) { this.passCode = passCode; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public Long getBusId() { return busId; }
    public void setBusId(Long busId) { this.busId = busId; }

    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }

    public Long getBoardingStopId() { return boardingStopId; }
    public void setBoardingStopId(Long boardingStopId) { this.boardingStopId = boardingStopId; }

    public String getBoardingStopName() { return boardingStopName; }
    public void setBoardingStopName(String boardingStopName) { this.boardingStopName = boardingStopName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getValidityStart() { return validityStart; }
    public void setValidityStart(LocalDate validityStart) { this.validityStart = validityStart; }

    public LocalDate getValidityEnd() { return validityEnd; }
    public void setValidityEnd(LocalDate validityEnd) { this.validityEnd = validityEnd; }

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
