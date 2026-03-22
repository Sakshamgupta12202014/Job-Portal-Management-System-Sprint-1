package com.jobportal.repository;

import com.jobportal.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByApplicantEmailAndJobId(String applicantEmail, Long jobId);
    List<Application> findByApplicantEmail(String applicantEmail);
    List<Application> findByJobId(Long jobId);
    long countByStatus(com.jobportal.model.ApplicationStatus status);
}
