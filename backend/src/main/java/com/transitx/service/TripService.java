package com.transitx.service;

import com.transitx.dto.TripDTO;
import com.transitx.entity.*;
import com.transitx.exception.BadRequestException;
import com.transitx.exception.ResourceNotFoundException;
import com.transitx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusLocationRepository locationRepository;

    @Autowired
    private BusAssignmentRepository assignmentRepository;

    @Transactional
    public TripDTO startTrip(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverId));

        if (driver.getAssignedBus() == null) {
            throw new BadRequestException("No bus is currently assigned to you");
        }

        Bus bus = driver.getAssignedBus();
        Optional<BusAssignment> assignOpt = assignmentRepository.findByDriverIdAndStatus(driverId, "ACTIVE");
        if (assignOpt.isEmpty()) {
            throw new BadRequestException("No active route assignment found for driver");
        }

        Route route = assignOpt.get().getRoute();

        // Check if there is already an ON_ROUTE trip
        Optional<Trip> active = tripRepository.findTopByDriverIdAndTripStatusOrderByStartTimeDesc(driverId, "ON_ROUTE");
        if (active.isPresent()) {
            return mapToDTO(active.get());
        }

        Trip trip = new Trip(bus, driver, route, LocalDateTime.now(), "ON_ROUTE", null);
        Trip saved = tripRepository.save(trip);

        // Update bus location status
        BusLocation loc = locationRepository.findByBusId(bus.getId()).orElse(null);
        if (loc != null) {
            loc.setStatus("ON_ROUTE");
            loc.setLastUpdated(LocalDateTime.now());
            locationRepository.save(loc);
        }

        return mapToDTO(saved);
    }

    @Transactional
    public TripDTO endTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + tripId));

        trip.setTripStatus("COMPLETED");
        trip.setEndTime(LocalDateTime.now());
        Trip saved = tripRepository.save(trip);

        // Update bus location status
        BusLocation loc = locationRepository.findByBusId(trip.getBus().getId()).orElse(null);
        if (loc != null) {
            loc.setStatus("REACHED_DESTINATION");
            loc.setLastUpdated(LocalDateTime.now());
            locationRepository.save(loc);
        }

        return mapToDTO(saved);
    }

    @Transactional
    public TripDTO updateTripStatus(Long tripId, String status) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found: " + tripId));

        trip.setTripStatus(status);
        if ("COMPLETED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
            trip.setEndTime(LocalDateTime.now());
        }
        Trip saved = tripRepository.save(trip);

        BusLocation loc = locationRepository.findByBusId(trip.getBus().getId()).orElse(null);
        if (loc != null) {
            loc.setStatus(status);
            loc.setLastUpdated(LocalDateTime.now());
            locationRepository.save(loc);
        }

        return mapToDTO(saved);
    }

    public List<TripDTO> getActiveTrips() {
        return tripRepository.findByTripStatus("ON_ROUTE").stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<TripDTO> getDriverTripHistory(Long driverId) {
        return tripRepository.findByDriverIdOrderByCreatedAtDesc(driverId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public TripDTO mapToDTO(Trip t) {
        TripDTO dto = new TripDTO();
        dto.setId(t.getId());
        dto.setBusId(t.getBus().getId());
        dto.setBusNumber(t.getBus().getBusNumber());
        dto.setDriverId(t.getDriver().getId());
        dto.setDriverName(t.getDriver().getFullName());
        dto.setRouteId(t.getRoute().getId());
        dto.setRouteName(t.getRoute().getRouteName());
        dto.setStartTime(t.getStartTime());
        dto.setEndTime(t.getEndTime());
        dto.setTripStatus(t.getTripStatus());
        if (t.getCurrentStop() != null) {
            dto.setCurrentStopName(t.getCurrentStop().getStopName());
        } else {
            dto.setCurrentStopName("Main Campus Bay");
        }
        return dto;
    }
}
