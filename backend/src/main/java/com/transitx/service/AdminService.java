package com.transitx.service;

import com.transitx.dto.AdminStatsDTO;
import com.transitx.entity.PassStatus;
import com.transitx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StudentBusPassRepository passRepository;

    @Autowired
    private TripRepository tripRepository;

    public AdminStatsDTO getDashboardStats() {
        AdminStatsDTO dto = new AdminStatsDTO();
        dto.setTotalStudents(studentRepository.count());
        dto.setTotalBuses(busRepository.count());
        dto.setTotalDrivers(driverRepository.count());
        dto.setTotalRoutes(routeRepository.count());
        dto.setTotalPassApplications(passRepository.count());
        dto.setPendingPassApplications(passRepository.countByStatus(PassStatus.PENDING));
        dto.setApprovedPassApplications(passRepository.countByStatus(PassStatus.APPROVED));
        dto.setActiveTrips(tripRepository.countByTripStatus("ON_ROUTE"));
        return dto;
    }
}
