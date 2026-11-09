package com.transitx.service;

import com.transitx.dto.DriverDTO;
import com.transitx.entity.*;
import com.transitx.exception.BadRequestException;
import com.transitx.exception.ResourceNotFoundException;
import com.transitx.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private BusAssignmentRepository assignmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public DriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return mapToDTO(driver);
    }

    @Transactional
    public DriverDTO addDriver(DriverDTO dto) {
        if (driverRepository.existsByDriverCode(dto.getDriverCode())) {
            throw new BadRequestException("Driver code already exists: " + dto.getDriverCode());
        }

        User user = new User(
                dto.getDriverCode(),
                dto.getDriverCode().toLowerCase() + "@transitx.edu",
                passwordEncoder.encode("driver123"),
                Role.ROLE_DRIVER,
                dto.getFullName(),
                dto.getPhone()
        );
        User savedUser = userRepository.save(user);

        Bus bus = null;
        if (dto.getAssignedBusId() != null) {
            bus = busRepository.findById(dto.getAssignedBusId()).orElse(null);
        }

        Driver driver = new Driver(
                savedUser,
                dto.getDriverCode(),
                dto.getFullName(),
                dto.getPhone(),
                dto.getLicenseNo(),
                dto.getExperienceYears() != null ? dto.getExperienceYears() : 5,
                bus
        );
        return mapToDTO(driverRepository.save(driver));
    }

    @Transactional
    public DriverDTO updateDriver(Long id, DriverDTO dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        driver.setFullName(dto.getFullName());
        driver.setPhone(dto.getPhone());
        if (dto.getLicenseNo() != null) driver.setLicenseNo(dto.getLicenseNo());
        if (dto.getExperienceYears() != null) driver.setExperienceYears(dto.getExperienceYears());

        if (dto.getAssignedBusId() != null) {
            Bus bus = busRepository.findById(dto.getAssignedBusId()).orElse(null);
            driver.setAssignedBus(bus);
        }

        return mapToDTO(driverRepository.save(driver));
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        User user = driver.getUser();
        driverRepository.delete(driver);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public DriverDTO mapToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setDriverCode(driver.getDriverCode());
        dto.setFullName(driver.getFullName());
        dto.setPhone(driver.getPhone());
        dto.setLicenseNo(driver.getLicenseNo());
        dto.setExperienceYears(driver.getExperienceYears());

        if (driver.getAssignedBus() != null) {
            dto.setAssignedBusId(driver.getAssignedBus().getId());
            dto.setAssignedBusNumber(driver.getAssignedBus().getBusNumber());
        } else {
            dto.setAssignedBusNumber("Unassigned");
        }

        Optional<BusAssignment> assignment = assignmentRepository.findByDriverIdAndStatus(driver.getId(), "ACTIVE");
        if (assignment.isPresent()) {
            dto.setAssignedRouteName(assignment.get().getRoute().getRouteName());
        } else {
            dto.setAssignedRouteName("Unassigned");
        }
        return dto;
    }
}
