package com.transitx.service;

import com.transitx.dto.BusPassApplyRequest;
import com.transitx.dto.BusPassDTO;
import com.transitx.entity.*;
import com.transitx.exception.BadRequestException;
import com.transitx.exception.ResourceNotFoundException;
import com.transitx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BusPassService {

    @Autowired
    private StudentBusPassRepository passRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private BusStopRepository stopRepository;

    @Autowired
    private BusAssignmentRepository assignmentRepository;

    @Transactional
    public BusPassDTO applyForPass(Long studentId, BusPassApplyRequest req, String idProofPath) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Route route = routeRepository.findById(req.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + req.getRouteId()));

        BusStop stop = stopRepository.findById(req.getBoardingStopId())
                .orElseThrow(() -> new ResourceNotFoundException("Boarding stop not found: " + req.getBoardingStopId()));

        // Check if student already has a pending or approved pass
        Optional<StudentBusPass> existing = passRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId);
        if (existing.isPresent() && existing.get().getStatus() == PassStatus.PENDING) {
            throw new BadRequestException("You already have a pending bus pass application");
        }

        // Try to pre-assign active bus for this route if exists
        Bus assignedBus = null;
        List<BusAssignment> assignments = assignmentRepository.findByRouteId(route.getId());
        if (!assignments.isEmpty()) {
            assignedBus = assignments.get(0).getBus();
        }

        String passCode = "TXPASS-" + LocalDate.now().getYear() + "-" + student.getRegNo().toUpperCase();

        StudentBusPass pass = new StudentBusPass(
                passCode,
                student,
                route,
                assignedBus,
                stop,
                PassStatus.PENDING,
                null,
                null,
                idProofPath != null ? idProofPath : "/uploads/default_id_proof.jpg",
                null
        );

        return mapToDTO(passRepository.save(pass));
    }

    public List<BusPassDTO> getStudentPasses(Long studentId) {
        return passRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public BusPassDTO getLatestStudentPass(Long studentId) {
        StudentBusPass pass = passRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("No bus pass application found for this student"));
        return mapToDTO(pass);
    }

    public List<BusPassDTO> getAllApplications(String status) {
        if (status != null && !status.isEmpty()) {
            return passRepository.findByStatus(PassStatus.valueOf(status.toUpperCase())).stream()
                    .map(this::mapToDTO).collect(Collectors.toList());
        }
        return passRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public BusPassDTO approvePass(Long passId, Long busId, Integer validityMonths) {
        StudentBusPass pass = passRepository.findById(passId)
                .orElseThrow(() -> new ResourceNotFoundException("Pass application not found: " + passId));

        if (busId != null) {
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found: " + busId));
            pass.setBus(bus);
            pass.getStudent().setAssignedBus(bus);
        } else if (pass.getBus() == null) {
            List<BusAssignment> assignments = assignmentRepository.findByRouteId(pass.getRoute().getId());
            if (!assignments.isEmpty()) {
                pass.setBus(assignments.get(0).getBus());
                pass.getStudent().setAssignedBus(assignments.get(0).getBus());
            }
        }

        pass.getStudent().setAssignedRoute(pass.getRoute());
        studentRepository.save(pass.getStudent());

        LocalDate start = LocalDate.now();
        int months = (validityMonths != null && validityMonths > 0) ? validityMonths : 6;
        LocalDate end = start.plusMonths(months);

        pass.setStatus(PassStatus.APPROVED);
        pass.setValidityStart(start);
        pass.setValidityEnd(end);
        pass.setRejectionReason(null);

        // Verification QR code payload
        String qrData = String.format("TRANSITX|PASS:%s|STUDENT:%s|ROUTE:%s|BUS:%s|BOARDING:%s|VALID_TILL:%s",
                pass.getPassCode(),
                pass.getStudent().getRegNo(),
                pass.getRoute().getRouteCode(),
                pass.getBus() != null ? pass.getBus().getBusNumber() : "TBD",
                pass.getBoardingStop().getStopName(),
                end.toString());

        pass.setQrCodeData(qrData);

        return mapToDTO(passRepository.save(pass));
    }

    @Transactional
    public BusPassDTO rejectPass(Long passId, String reason) {
        StudentBusPass pass = passRepository.findById(passId)
                .orElseThrow(() -> new ResourceNotFoundException("Pass application not found: " + passId));

        pass.setStatus(PassStatus.REJECTED);
        pass.setRejectionReason(reason != null && !reason.trim().isEmpty() ? reason : "Application details incomplete");
        return mapToDTO(passRepository.save(pass));
    }

    public BusPassDTO verifyPass(String passCode) {
        StudentBusPass pass = passRepository.findByPassCode(passCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid bus pass code: " + passCode));
        return mapToDTO(pass);
    }

    public BusPassDTO mapToDTO(StudentBusPass pass) {
        BusPassDTO dto = new BusPassDTO();
        dto.setId(pass.getId());
        dto.setPassCode(pass.getPassCode());
        dto.setStudentId(pass.getStudent().getId());
        dto.setStudentName(pass.getStudent().getFullName());
        dto.setRegNo(pass.getStudent().getRegNo());
        dto.setDepartment(pass.getStudent().getDepartment());
        dto.setRouteId(pass.getRoute().getId());
        dto.setRouteName(pass.getRoute().getRouteName());
        dto.setRouteCode(pass.getRoute().getRouteCode());

        if (pass.getBus() != null) {
            dto.setBusId(pass.getBus().getId());
            dto.setBusNumber(pass.getBus().getBusNumber());
        } else {
            dto.setBusNumber("Pending Bus Allocation");
        }

        if (pass.getBoardingStop() != null) {
            dto.setBoardingStopId(pass.getBoardingStop().getId());
            dto.setBoardingStopName(pass.getBoardingStop().getStopName());
        }

        dto.setStatus(pass.getStatus().name());
        dto.setValidityStart(pass.getValidityStart());
        dto.setValidityEnd(pass.getValidityEnd());
        dto.setQrCodeData(pass.getQrCodeData());
        dto.setRejectionReason(pass.getRejectionReason());
        dto.setCreatedAt(pass.getCreatedAt());
        return dto;
    }
}
