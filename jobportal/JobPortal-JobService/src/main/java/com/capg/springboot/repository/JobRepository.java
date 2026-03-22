package com.capg.springboot.repository;

import com.capg.springboot.entity.Job;
import com.capg.springboot.enums.JobStatus;
import com.capg.springboot.enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // All ACTIVE jobs — used for public listing
    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    // Recruiter's own jobs
    Page<Job> findByPostedByAndStatusNot(Long postedBy, JobStatus status, Pageable pageable);

    // Full search with optional filters — all params nullable
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:jobType IS NULL OR j.jobType = :jobType) " +
           "AND (:experienceYears IS NULL OR j.experienceYears <= :experienceYears) " +
           "AND (:salaryMin IS NULL OR j.salaryMin >= :salaryMin) " +
           "AND (:salaryMax IS NULL OR j.salaryMax <= :salaryMax)")
    Page<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("jobType") JobType jobType,
                         @Param("experienceYears") Integer experienceYears,
                         @Param("salaryMin") BigDecimal salaryMin,
                         @Param("salaryMax") BigDecimal salaryMax,
                         Pageable pageable);

    List<Job> findByPostedBy(Long postedBy);
}
