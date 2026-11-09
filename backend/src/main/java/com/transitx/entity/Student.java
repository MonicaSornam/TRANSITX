package com.transitx.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "reg_no", nullable = false, unique = true, length = 50)
    private String regNo;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "year_of_study", nullable = false, length = 20)
    private String yearOfStudy;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_bus_id")
    private Bus assignedBus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_route_id")
    private Route assignedRoute;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Student() {}

    public Student(User user, String regNo, String department, String yearOfStudy,
                   String fullName, String email, String phone) {
        this.user = user;
        this.regNo = regNo;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public Bus getAssignedBus() { return assignedBus; }
    public void setAssignedBus(Bus assignedBus) { this.assignedBus = assignedBus; }

    public Route getAssignedRoute() { return assignedRoute; }
    public void setAssignedRoute(Route assignedRoute) { this.assignedRoute = assignedRoute; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
