package com.capg.springboot.repository;

import com.capg.springboot.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // All applications by a seeker — for GET /api/applications/user
    List<Application> findByUserId(Long userId);

    // All applications for a specific job — for recruiter view
    List<Application> findByJobId(Long jobId);

    // Duplicate check — backs the UNIQUE(user_id, job_id) constraint
    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    Optional<Application> findByIdAndUserId(Long id, Long userId);
}
