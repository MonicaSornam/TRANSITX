package com.transitx.controller;

import com.transitx.dto.ApiResponse;
import com.transitx.dto.LocationUpdateDTO;
import com.transitx.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @Autowired
    private TrackingService trackingService;

    @GetMapping("/bus/{busId}")
    public ResponseEntity<ApiResponse<LocationUpdateDTO>> getBusLocation(@PathVariable Long busId) {
        return ResponseEntity.ok(ApiResponse.ok(trackingService.getBusLocation(busId)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<LocationUpdateDTO>>> getAllActiveLocations() {
        return ResponseEntity.ok(ApiResponse.ok(trackingService.getAllActiveLocations()));
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<LocationUpdateDTO>> updateLocation(@RequestBody Map<String, Object> payload) {
        Long busId = Long.valueOf(payload.get("busId").toString());
        BigDecimal lat = new BigDecimal(payload.get("latitude").toString());
        BigDecimal lng = new BigDecimal(payload.get("longitude").toString());
        BigDecimal speed = payload.get("speed") != null ? new BigDecimal(payload.get("speed").toString()) : BigDecimal.valueOf(35.0);
        String status = payload.get("status") != null ? payload.get("status").toString() : "ON_ROUTE";

        LocationUpdateDTO dto = trackingService.updateLocation(busId, lat, lng, speed, status);
        return ResponseEntity.ok(ApiResponse.ok("Location updated", dto));
    }

    @PostMapping("/simulate-step/{busId}")
    public ResponseEntity<ApiResponse<LocationUpdateDTO>> simulateStep(@PathVariable Long busId) {
        return ResponseEntity.ok(ApiResponse.ok("Simulated step updated", trackingService.simulateStep(busId)));
    }
}
