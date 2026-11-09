package com.transitx.repository;

import com.transitx.entity.BusAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface BusAssignmentRepository extends JpaRepository<BusAssignment, Long> {
    Optional<BusAssignment> findByDriverIdAndStatus(Long driverId, String status);
    Optional<BusAssignment> findByBusIdAndStatus(Long busId, String status);
    List<BusAssignment> findByRouteId(Long routeId);
}
