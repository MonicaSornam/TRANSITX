package com.transitx.controller;

import com.transitx.dto.ApiResponse;
import com.transitx.dto.BusDTO;
import com.transitx.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BusController {

    @Autowired
    private BusService busService;

    @GetMapping("/buses")
    public ResponseEntity<ApiResponse<List<BusDTO>>> getAllBuses() {
        return ResponseEntity.ok(ApiResponse.ok(busService.getAllBuses()));
    }

    @GetMapping("/buses/{id}")
    public ResponseEntity<ApiResponse<BusDTO>> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(busService.getBusById(id)));
    }

    @PostMapping("/admin/buses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BusDTO>> addBus(@RequestBody BusDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Bus created successfully", busService.addBus(dto)));
    }

    @PutMapping("/admin/buses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BusDTO>> updateBus(@PathVariable Long id, @RequestBody BusDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Bus updated successfully", busService.updateBus(id, dto)));
    }

    @DeleteMapping("/admin/buses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok(ApiResponse.ok("Bus deleted successfully", null));
    }
}
