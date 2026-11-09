package com.transitx.service;

import com.transitx.dto.StudentDTO;
import com.transitx.entity.*;
import com.transitx.exception.ResourceNotFoundException;
import com.transitx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteRepository routeRepository;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return mapToDTO(student);
    }

    public StudentDTO getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for user: " + userId));
        return mapToDTO(student);
    }

    @Transactional
    public StudentDTO assignBusAndRoute(Long studentId, Long busId, Long routeId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (busId != null) {
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));
            student.setAssignedBus(bus);
        }
        if (routeId != null) {
            Route route = routeRepository.findById(routeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));
            student.setAssignedRoute(route);
        }

        return mapToDTO(studentRepository.save(student));
    }

    public StudentDTO mapToDTO(Student s) {
        StudentDTO dto = new StudentDTO();
        dto.setId(s.getId());
        dto.setRegNo(s.getRegNo());
        dto.setDepartment(s.getDepartment());
        dto.setYearOfStudy(s.getYearOfStudy());
        dto.setFullName(s.getFullName());
        dto.setEmail(s.getEmail());
        dto.setPhone(s.getPhone());

        if (s.getAssignedBus() != null) {
            dto.setAssignedBusId(s.getAssignedBus().getId());
            dto.setAssignedBusNumber(s.getAssignedBus().getBusNumber());
        } else {
            dto.setAssignedBusNumber("Not Assigned");
        }

        if (s.getAssignedRoute() != null) {
            dto.setAssignedRouteId(s.getAssignedRoute().getId());
            dto.setAssignedRouteName(s.getAssignedRoute().getRouteName());
        } else {
            dto.setAssignedRouteName("Not Assigned");
        }
        return dto;
    }
}
