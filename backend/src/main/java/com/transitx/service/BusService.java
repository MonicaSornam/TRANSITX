package com.transitx.service;

import com.transitx.dto.ApiResponse;
import com.transitx.dto.BusDTO;
import com.transitx.entity.Bus;
import com.transitx.entity.BusAssignment;
import com.transitx.entity.BusStatus;
import com.transitx.exception.BadRequestException;
import com.transitx.exception.ResourceNotFoundException;
import com.transitx.repository.BusAssignmentRepository;
import com.transitx.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private BusAssignmentRepository assignmentRepository;

    public List<BusDTO> getAllBuses() {
        return busRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public BusDTO getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));
        return mapToDTO(bus);
    }

    @Transactional
    public BusDTO addBus(BusDTO dto) {
        if (busRepository.existsByBusNumber(dto.getBusNumber())) {
            throw new BadRequestException("Bus number " + dto.getBusNumber() + " already exists");
        }
        Bus bus = new Bus(
                dto.getBusNumber(),
                dto.getCapacity() != null ? dto.getCapacity() : 40,
                dto.getStatus() != null ? BusStatus.valueOf(dto.getStatus()) : BusStatus.ACTIVE,
                dto.getModelPlate()
        );
        return mapToDTO(busRepository.save(bus));
    }

    @Transactional
    public BusDTO updateBus(Long id, BusDTO dto) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));

        if (!bus.getBusNumber().equalsIgnoreCase(dto.getBusNumber()) &&
                busRepository.existsByBusNumber(dto.getBusNumber())) {
            throw new BadRequestException("Bus number " + dto.getBusNumber() + " already in use");
        }

        bus.setBusNumber(dto.getBusNumber());
        if (dto.getCapacity() != null) bus.setCapacity(dto.getCapacity());
        if (dto.getStatus() != null) bus.setStatus(BusStatus.valueOf(dto.getStatus()));
        if (dto.getModelPlate() != null) bus.setModelPlate(dto.getModelPlate());

        return mapToDTO(busRepository.save(bus));
    }

    @Transactional
    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bus not found with id: " + id);
        }
        busRepository.deleteById(id);
    }

    public BusDTO mapToDTO(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setId(bus.getId());
        dto.setBusNumber(bus.getBusNumber());
        dto.setCapacity(bus.getCapacity());
        dto.setStatus(bus.getStatus().name());
        dto.setModelPlate(bus.getModelPlate());

        Optional<BusAssignment> assignment = assignmentRepository.findByBusIdAndStatus(bus.getId(), "ACTIVE");
        if (assignment.isPresent()) {
            dto.setCurrentRouteName(assignment.get().getRoute().getRouteName());
            dto.setCurrentDriverName(assignment.get().getDriver().getFullName());
        } else {
            dto.setCurrentRouteName("Unassigned");
            dto.setCurrentDriverName("Unassigned");
        }
        return dto;
    }
}
