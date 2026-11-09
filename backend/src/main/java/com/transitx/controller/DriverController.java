package com.transitx.controller;

import com.transitx.dto.ApiResponse;
import com.transitx.dto.DriverDTO;
import com.transitx.security.UserPrincipal;
import com.transitx.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<List<DriverDTO>>> getAllDrivers() {
        return ResponseEntity.ok(ApiResponse.ok(driverService.getAllDrivers()));
    }

    @GetMapping("/drivers/{id}")
    public ResponseEntity<ApiResponse<DriverDTO>> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(driverService.getDriverById(id)));
    }

    @GetMapping("/driver/dashboard")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO>> getDriverDashboard(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        DriverDTO driver = driverService.getAllDrivers().stream()
                .filter(d -> d.getDriverCode().equalsIgnoreCase(userPrincipal.getUsername()))
                .findFirst()
                .orElse(null);
        return ResponseEntity.ok(ApiResponse.ok(driver));
    }

    @PostMapping("/admin/drivers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO>> addDriver(@RequestBody DriverDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Driver added successfully", driverService.addDriver(dto)));
    }

    @PutMapping("/admin/drivers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DriverDTO>> updateDriver(@PathVariable Long id, @RequestBody DriverDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Driver updated successfully", driverService.updateDriver(id, dto)));
    }

    @DeleteMapping("/admin/drivers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok(ApiResponse.ok("Driver deleted successfully", null));
    }
}
