package com.transitx.service;

import com.transitx.dto.LocationUpdateDTO;
import com.transitx.entity.*;
import com.transitx.exception.ResourceNotFoundException;
import com.transitx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrackingService {

    @Autowired
    private BusLocationRepository locationRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;

    @Autowired
    private BusAssignmentRepository assignmentRepository;

    public LocationUpdateDTO getBusLocation(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));

        BusLocation loc = locationRepository.findByBusId(busId)
                .orElseGet(() -> {
                    // Default to first stop or center
                    BusLocation defaultLoc = new BusLocation(
                            bus,
                            new BigDecimal("12.9716000"),
                            new BigDecimal("77.5946000"),
                            BigDecimal.valueOf(35.0),
                            "ON_ROUTE",
                            true
                    );
                    return locationRepository.save(defaultLoc);
                });

        return mapToDTO(loc);
    }

    public List<LocationUpdateDTO> getAllActiveLocations() {
        List<BusLocation> list = locationRepository.findAll();
        List<LocationUpdateDTO> dtoList = new ArrayList<>();
        for (BusLocation loc : list) {
            dtoList.add(mapToDTO(loc));
        }
        return dtoList;
    }

    @Transactional
    public LocationUpdateDTO updateLocation(Long busId, BigDecimal lat, BigDecimal lng, BigDecimal speed, String status) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));

        BusLocation loc = locationRepository.findByBusId(busId)
                .orElseGet(() -> new BusLocation(bus, lat, lng, speed, status, false));

        loc.setLatitude(lat);
        loc.setLongitude(lng);
        if (speed != null) loc.setSpeed(speed);
        if (status != null) loc.setStatus(status);
        loc.setLastUpdated(LocalDateTime.now());
        loc.setIsSimulated(false); // Hardware or manual GPS

        return mapToDTO(locationRepository.save(loc));
    }

    @Transactional
    public LocationUpdateDTO simulateStep(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));

        Optional<BusAssignment> assignmentOpt = assignmentRepository.findByBusIdAndStatus(busId, "ACTIVE");
        if (assignmentOpt.isEmpty()) {
            return getBusLocation(busId);
        }

        Route route = assignmentOpt.get().getRoute();
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequenceAsc(route.getId());
        if (stops.isEmpty()) {
            return getBusLocation(busId);
        }

        BusLocation loc = locationRepository.findByBusId(busId)
                .orElseGet(() -> new BusLocation(
                        bus,
                        stops.get(0).getStop().getLatitude(),
                        stops.get(0).getStop().getLongitude(),
                        BigDecimal.valueOf(30),
                        "ON_ROUTE",
                        true
                ));

        // Find nearest stop and move towards next stop
        int currentIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < stops.size(); i++) {
            double d = distance(
                    loc.getLatitude().doubleValue(), loc.getLongitude().doubleValue(),
                    stops.get(i).getStop().getLatitude().doubleValue(), stops.get(i).getStop().getLongitude().doubleValue()
            );
            if (d < minDistance) {
                minDistance = d;
                currentIndex = i;
            }
        }

        int targetIndex = (currentIndex + 1) % stops.size();
        BusStop target = stops.get(targetIndex).getStop();

        // Step 15% towards the target stop
        double curLat = loc.getLatitude().doubleValue();
        double curLng = loc.getLongitude().doubleValue();
        double targetLat = target.getLatitude().doubleValue();
        double targetLng = target.getLongitude().doubleValue();

        double nextLat = curLat + (targetLat - curLat) * 0.15;
        double nextLng = curLng + (targetLng - curLng) * 0.15;

        loc.setLatitude(BigDecimal.valueOf(nextLat).setScale(7, RoundingMode.HALF_UP));
        loc.setLongitude(BigDecimal.valueOf(nextLng).setScale(7, RoundingMode.HALF_UP));
        loc.setSpeed(BigDecimal.valueOf(32.5 + (Math.random() * 8.0)).setScale(2, RoundingMode.HALF_UP));
        loc.setStatus("ON_ROUTE");
        loc.setLastUpdated(LocalDateTime.now());
        loc.setIsSimulated(true);

        return mapToDTO(locationRepository.save(loc));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371 * c; // kilometers
    }

    public LocationUpdateDTO mapToDTO(BusLocation loc) {
        LocationUpdateDTO dto = new LocationUpdateDTO();
        dto.setBusId(loc.getBus().getId());
        dto.setBusNumber(loc.getBus().getBusNumber());
        dto.setLatitude(loc.getLatitude());
        dto.setLongitude(loc.getLongitude());
        dto.setSpeed(loc.getSpeed());
        dto.setStatus(loc.getStatus());
        dto.setLastUpdated(loc.getLastUpdated());
        dto.setIsSimulated(loc.getIsSimulated());

        Optional<BusAssignment> assignment = assignmentRepository.findByBusIdAndStatus(loc.getBus().getId(), "ACTIVE");
        if (assignment.isPresent()) {
            Route r = assignment.get().getRoute();
            dto.setRouteName(r.getRouteName());
            List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopSequenceAsc(r.getId());
            if (!stops.isEmpty()) {
                // Approximate ETA
                dto.setEstimatedArrival("12 mins (approx)");
                dto.setNextStopName(stops.get(Math.min(1, stops.size() - 1)).getStop().getStopName());
            }
        } else {
            dto.setRouteName("General Campus Route");
            dto.setEstimatedArrival("N/A");
            dto.setNextStopName("Main Campus Bay");
        }

        return dto;
    }
}
