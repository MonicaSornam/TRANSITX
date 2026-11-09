package com.transitx.controller;

import com.transitx.dto.ApiResponse;
import com.transitx.dto.RouteDTO;
import com.transitx.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<List<RouteDTO>>> getAllRoutes(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(ApiResponse.ok(routeService.searchRoutes(query)));
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<ApiResponse<RouteDTO>> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(routeService.getRouteById(id)));
    }

    @PostMapping("/admin/routes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RouteDTO>> addRoute(@RequestBody RouteDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Route created successfully", routeService.addRoute(dto)));
    }

    @PutMapping("/admin/routes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RouteDTO>> updateRoute(@PathVariable Long id, @RequestBody RouteDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Route updated successfully", routeService.updateRoute(id, dto)));
    }

    @DeleteMapping("/admin/routes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.ok(ApiResponse.ok("Route deleted successfully", null));
    }
}
