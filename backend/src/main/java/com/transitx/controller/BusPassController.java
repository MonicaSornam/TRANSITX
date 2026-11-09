package com.transitx.controller;

import com.transitx.dto.ApiResponse;
import com.transitx.dto.BusPassApplyRequest;
import com.transitx.dto.BusPassDTO;
import com.transitx.dto.StudentDTO;
import com.transitx.security.UserPrincipal;
import com.transitx.service.BusPassService;
import com.transitx.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bus-pass")
public class BusPassController {

    @Autowired
    private BusPassService busPassService;

    @Autowired
    private StudentService studentService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<BusPassDTO>> applyForPass(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("routeId") Long routeId,
            @RequestParam("boardingStopId") Long boardingStopId,
            @RequestParam(value = "idProof", required = false) MultipartFile idProof
    ) {
        StudentDTO student = studentService.getStudentByUserId(userPrincipal.getId());

        String idProofPath = "/uploads/default_id_proof.jpg";
        if (idProof != null && !idProof.isEmpty()) {
            try {
                Path uploadDir = Paths.get("./uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                String originalFilename = idProof.getOriginalFilename();
                String ext = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
                String fileName = "idproof_" + student.getRegNo() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
                Path targetLocation = uploadDir.resolve(fileName);
                Files.copy(idProof.getInputStream(), targetLocation);
                idProofPath = "/uploads/" + fileName;
            } catch (IOException e) {
                // fallback to default
            }
        }

        BusPassApplyRequest req = new BusPassApplyRequest();
        req.setRouteId(routeId);
        req.setBoardingStopId(boardingStopId);

        BusPassDTO pass = busPassService.applyForPass(student.getId(), req, idProofPath);
        return ResponseEntity.ok(ApiResponse.ok("Bus pass application submitted successfully", pass));
    }

    @GetMapping("/my-pass")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<BusPassDTO>> getMyLatestPass(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudentDTO student = studentService.getStudentByUserId(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok(busPassService.getLatestStudentPass(student.getId())));
    }

    @GetMapping("/my-passes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<BusPassDTO>>> getMyPasses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudentDTO student = studentService.getStudentByUserId(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok(busPassService.getStudentPasses(student.getId())));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BusPassDTO>>> getAllApplications(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(busPassService.getAllApplications(status)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BusPassDTO>> approvePass(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> payload) {
        Long busId = null;
        Integer validityMonths = 6;
        if (payload != null) {
            if (payload.get("busId") != null) busId = Long.valueOf(payload.get("busId").toString());
            if (payload.get("validityMonths") != null) validityMonths = Integer.valueOf(payload.get("validityMonths").toString());
        }
        BusPassDTO pass = busPassService.approvePass(id, busId, validityMonths);
        return ResponseEntity.ok(ApiResponse.ok("Bus pass approved successfully", pass));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BusPassDTO>> rejectPass(@PathVariable Long id, @RequestBody(required = false) Map<String, String> payload) {
        String reason = payload != null ? payload.get("reason") : "Application details incomplete";
        BusPassDTO pass = busPassService.rejectPass(id, reason);
        return ResponseEntity.ok(ApiResponse.ok("Bus pass rejected", pass));
    }

    @GetMapping("/verify/{passCode}")
    public ResponseEntity<ApiResponse<BusPassDTO>> verifyPass(@PathVariable String passCode) {
        return ResponseEntity.ok(ApiResponse.ok("Bus pass verified", busPassService.verifyPass(passCode)));
    }
}
