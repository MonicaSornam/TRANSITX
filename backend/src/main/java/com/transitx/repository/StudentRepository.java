package com.transitx.repository;

import com.transitx.entity.Student;
import com.transitx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegNo(String regNo);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByUser(User user);
    Optional<Student> findByUserId(Long userId);
    Boolean existsByRegNo(String regNo);
    Boolean existsByEmail(String email);
    List<Student> findByAssignedBusId(Long busId);
    List<Student> findByAssignedRouteId(Long routeId);
}
