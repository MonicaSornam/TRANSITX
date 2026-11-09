package com.transitx.controller;

import com.transitx.dto.ApiResponse;
import com.transitx.dto.StudentDTO;
import com.transitx.security.UserPrincipal;
import com.transitx.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/student/profile")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<StudentDTO>> getProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        StudentDTO student = studentService.getStudentByUserId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(student));
    }

    @GetMapping("/admin/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getAllStudents() {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAllStudents()));
    }

    @PostMapping("/admin/students/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentDTO>> assignBusAndRoute(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long busId = payload.get("busId");
        Long routeId = payload.get("routeId");
        StudentDTO dto = studentService.assignBusAndRoute(id, busId, routeId);
        return ResponseEntity.ok(ApiResponse.ok("Student allocation updated", dto));
    }
}
