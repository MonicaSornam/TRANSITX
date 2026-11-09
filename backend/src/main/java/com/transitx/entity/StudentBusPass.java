package com.transitx.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_bus_pass")
public class StudentBusPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pass_code", nullable = false, unique = true, length = 64)
    private String passCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "boarding_stop_id", nullable = false)
    private BusStop boardingStop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PassStatus status = PassStatus.PENDING;

    @Column(name = "validity_start")
    private LocalDate validityStart;

    @Column(name = "validity_end")
    private LocalDate validityEnd;

    @Column(name = "id_proof_path", length = 255)
    private String idProofPath;

    @Column(name = "qr_code_data", length = 255)
    private String qrCodeData;

    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public StudentBusPass() {}

    public StudentBusPass(String passCode, Student student, Route route, Bus bus,
                          BusStop boardingStop, PassStatus status, LocalDate validityStart,
                          LocalDate validityEnd, String idProofPath, String qrCodeData) {
        this.passCode = passCode;
        this.student = student;
        this.route = route;
        this.bus = bus;
        this.boardingStop = boardingStop;
        this.status = status;
        this.validityStart = validityStart;
        this.validityEnd = validityEnd;
        this.idProofPath = idProofPath;
        this.qrCodeData = qrCodeData;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPassCode() { return passCode; }
    public void setPassCode(String passCode) { this.passCode = passCode; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { this.bus = bus; }

    public BusStop getBoardingStop() { return boardingStop; }
    public void setBoardingStop(BusStop boardingStop) { this.boardingStop = boardingStop; }

    public PassStatus getStatus() { return status; }
    public void setStatus(PassStatus status) { this.status = status; }

    public LocalDate getValidityStart() { return validityStart; }
    public void setValidityStart(LocalDate validityStart) { this.validityStart = validityStart; }

    public LocalDate getValidityEnd() { return validityEnd; }
    public void setValidityEnd(LocalDate validityEnd) { this.validityEnd = validityEnd; }

    public String getIdProofPath() { return idProofPath; }
    public void setIdProofPath(String idProofPath) { this.idProofPath = idProofPath; }

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
