package com.transitx.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String role;
    private String username;
    private String fullName;
    private String email;
    private Long studentId;
    private Long driverId;

    public AuthResponse() {}

    public AuthResponse(String token, String role, String username, String fullName, String email, Long studentId, Long driverId) {
        this.token = token;
        this.type = "Bearer";
        this.role = role;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.studentId = studentId;
        this.driverId = driverId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
}
