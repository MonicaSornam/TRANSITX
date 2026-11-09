package com.transitx.repository;

import com.transitx.entity.Driver;
import com.transitx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByDriverCode(String driverCode);
    Optional<Driver> findByUser(User user);
    Optional<Driver> findByUserId(Long userId);
    Optional<Driver> findByAssignedBusId(Long busId);
    Boolean existsByDriverCode(String driverCode);
}
