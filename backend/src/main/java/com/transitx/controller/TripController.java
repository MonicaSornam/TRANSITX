package com.transitx.controller;

import com.transitx.dto.ApiResponse;
import com.transitx.dto.TripDTO;
import com.transitx.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TripDTO>> startTrip(@RequestBody Map<String, Long> payload) {
        Long driverId = payload.get("driverId");
        return ResponseEntity.ok(ApiResponse.ok("Trip started successfully", tripService.startTrip(driverId)));
    }

    @PostMapping("/end/{tripId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TripDTO>> endTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(ApiResponse.ok("Trip ended successfully", tripService.endTrip(tripId)));
    }

    @PostMapping("/status/{tripId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public ResponseEntity<ApiResponse<TripDTO>> updateTripStatus(@PathVariable Long tripId, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        return ResponseEntity.ok(ApiResponse.ok("Trip status updated", tripService.updateTripStatus(tripId, status)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<TripDTO>>> getActiveTrips() {
        return ResponseEntity.ok(ApiResponse.ok(tripService.getActiveTrips()));
    }

    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<TripDTO>>> getDriverHistory(@PathVariable Long driverId) {
        return ResponseEntity.ok(ApiResponse.ok(tripService.getDriverTripHistory(driverId)));
    }
}
