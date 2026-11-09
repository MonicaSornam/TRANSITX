package com.transitx.repository;

import com.transitx.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
    Optional<BusLocation> findByBusId(Long busId);
    List<BusLocation> findByStatusNot(String status);
}
