package com.transitx.repository;

import com.transitx.entity.StudentBusPass;
import com.transitx.entity.PassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface StudentBusPassRepository extends JpaRepository<StudentBusPass, Long> {
    Optional<StudentBusPass> findByPassCode(String passCode);
    List<StudentBusPass> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    Optional<StudentBusPass> findTopByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<StudentBusPass> findByStatus(PassStatus status);
    long countByStatus(PassStatus status);
}
