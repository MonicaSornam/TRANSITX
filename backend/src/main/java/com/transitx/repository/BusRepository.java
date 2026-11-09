package com.transitx.repository;

import com.transitx.entity.Bus;
import com.transitx.entity.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByBusNumber(String busNumber);
    Boolean existsByBusNumber(String busNumber);
    List<Bus> findByStatus(BusStatus status);
}
