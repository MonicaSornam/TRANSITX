package com.transitx.dto;

public class AdminStatsDTO {
    private long totalStudents;
    private long totalBuses;
    private long totalDrivers;
    private long totalRoutes;
    private long totalPassApplications;
    private long pendingPassApplications;
    private long approvedPassApplications;
    private long activeTrips;

    public AdminStatsDTO() {}

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalBuses() { return totalBuses; }
    public void setTotalBuses(long totalBuses) { this.totalBuses = totalBuses; }

    public long getTotalDrivers() { return totalDrivers; }
    public void setTotalDrivers(long totalDrivers) { this.totalDrivers = totalDrivers; }

    public long getTotalRoutes() { return totalRoutes; }
    public void setTotalRoutes(long totalRoutes) { this.totalRoutes = totalRoutes; }

    public long getTotalPassApplications() { return totalPassApplications; }
    public void setTotalPassApplications(long totalPassApplications) { this.totalPassApplications = totalPassApplications; }

    public long getPendingPassApplications() { return pendingPassApplications; }
    public void setPendingPassApplications(long pendingPassApplications) { this.pendingPassApplications = pendingPassApplications; }

    public long getApprovedPassApplications() { return approvedPassApplications; }
    public void setApprovedPassApplications(long approvedPassApplications) { this.approvedPassApplications = approvedPassApplications; }

    public long getActiveTrips() { return activeTrips; }
    public void setActiveTrips(long activeTrips) { this.activeTrips = activeTrips; }
}
