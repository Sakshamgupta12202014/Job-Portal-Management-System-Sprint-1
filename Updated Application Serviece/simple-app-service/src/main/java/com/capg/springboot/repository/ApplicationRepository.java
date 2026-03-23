package com.capg.springboot.repository;

import com.capg.springboot.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Spring Data JPA generates all SQL automatically from method names
// We don't write any SQL ourselves here

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Get all applications submitted by a specific seeker
    // SQL: SELECT * FROM applications WHERE user_id = ?
    List<Application> findByUserId(Long userId);

    // Get all applications for a specific job
    // SQL: SELECT * FROM applications WHERE job_id = ?
    List<Application> findByJobId(Long jobId);

    // Check if a seeker already applied to a job (for duplicate check)
    // SQL: SELECT COUNT(*) > 0 FROM applications WHERE user_id = ? AND job_id = ?
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    // Get a specific application but only if it belongs to a specific seeker
    // Used so seekers cannot view other seekers' applications
    // SQL: SELECT * FROM applications WHERE id = ? AND user_id = ?
    Optional<Application> findByIdAndUserId(Long id, Long userId);

}
