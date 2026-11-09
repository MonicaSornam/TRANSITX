package com.transitx.service;

import com.transitx.dto.RouteDTO;
import com.transitx.dto.StopDTO;
import com.transitx.entity.*;
import com.transitx.exception.BadRequestException;
import com.transitx.exception.ResourceNotFoundException;
import com.transitx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteStopRepository routeStopRepository;

    @Autowired
    private BusStopRepository busStopRepository;

    @Autowired
    private BusAssignmentRepository assignmentRepository;

    public List<RouteDTO> getAllRoutes() {
        return routeRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<RouteDTO> searchRoutes(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllRoutes();
        }
        return routeRepository.searchRoutes(query.trim()).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public RouteDTO getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return mapToDTO(route);
    }

    @Transactional
    public RouteDTO addRoute(RouteDTO dto) {
        if (routeRepository.existsByRouteCode(dto.getRouteCode())) {
            throw new BadRequestException("Route code " + dto.getRouteCode() + " already exists");
        }
        Route route = new Route(
                dto.getRouteName(),
                dto.getRouteCode(),
                dto.getStartPoint(),
                dto.getEndPoint(),
                dto.getDepartureTime(),
                dto.getArrivalTime(),
                dto.getEstDuration(),
                dto.getDistanceKm() != null ? dto.getDistanceKm() : BigDecimal.ZERO
        );
        Route saved = routeRepository.save(route);

        if (dto.getStops() != null && !dto.getStops().isEmpty()) {
            for (StopDTO s : dto.getStops()) {
                BusStop stop = new BusStop(s.getStopName(), s.getLatitude(), s.getLongitude(), s.getLandmark());
                BusStop savedStop = busStopRepository.save(stop);
                RouteStop rs = new RouteStop(saved, savedStop, s.getSequence(), s.getPickupTime());
                routeStopRepository.save(rs);
            }
        }

        return mapToDTO(saved);
    }

    @Transactional
    public RouteDTO updateRoute(Long id, RouteDTO dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        route.setRouteName(dto.getRouteName());
        route.setStartPoint(dto.getStartPoint());
        route.setEndPoint(dto.getEndPoint());
        route.setDepartureTime(dto.getDepartureTime());
        route.setArrivalTime(dto.getArrivalTime());
        route.setEstDuration(dto.getEstDuration());
        if (dto.getDistanceKm() != null) route.setDistanceKm(dto.getDistanceKm());

        return mapToDTO(routeRepository.save(route));
    }

    @Transactional
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route not found with id: " + id);
        }
        routeStopRepository.deleteByRouteId(id);
        routeRepository.deleteById(id);
    }

    public RouteDTO mapToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setId(route.getId());
        dto.setRouteName(route.getRouteName());
        dto.setRouteCode(route.getRouteCode());
        dto.setStartPoint(route.getStartPoint());
        dto.setEndPoint(route.getEndPoint());
        dto.setDepartureTime(route.getDepartureTime());
        dto.setArrivalTime(route.getArrivalTime());
        dto.setEstDuration(route.getEstDuration());
        dto.setDistanceKm(route.getDistanceKm());

        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopSequenceAsc(route.getId());
        dto.setTotalStops(routeStops.size());

        List<StopDTO> stops = routeStops.stream().map(rs -> new StopDTO(
                rs.getStop().getId(),
                rs.getStop().getStopName(),
                rs.getStop().getLatitude(),
                rs.getStop().getLongitude(),
                rs.getStop().getLandmark(),
                rs.getStopSequence(),
                rs.getPickupTime()
        )).collect(Collectors.toList());
        dto.setStops(stops);

        List<BusAssignment> assignments = assignmentRepository.findByRouteId(route.getId());
        if (!assignments.isEmpty()) {
            BusAssignment ba = assignments.get(0);
            dto.setAssignedBusNumber(ba.getBus().getBusNumber());
            dto.setAssignedDriverName(ba.getDriver().getFullName());
        } else {
            dto.setAssignedBusNumber("Unassigned");
            dto.setAssignedDriverName("Unassigned");
        }

        return dto;
    }
}
