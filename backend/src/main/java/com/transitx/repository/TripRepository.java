package com.transitx.repository;

import com.transitx.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByTripStatus(String tripStatus);
    Optional<Trip> findTopByBusIdAndTripStatusOrderByStartTimeDesc(Long busId, String tripStatus);
    Optional<Trip> findTopByDriverIdAndTripStatusOrderByStartTimeDesc(Long driverId, String tripStatus);
    List<Trip> findByDriverIdOrderByCreatedAtDesc(Long driverId);
    long countByTripStatus(String tripStatus);
}
