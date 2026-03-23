package com.jobportal.job.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobportal.job.entity.Job;
import com.jobportal.job.enums.JobStatus;
import com.jobportal.job.enums.JobType;

@Repository
public interface JobRepository extends JpaRepository<Job , Long> {
	
	Page<Job> findByStatusNot(JobStatus status, Pageable pageable);
	 
    List<Job> findByPostedByAndStatusNot(Long postedBy, JobStatus status);
 
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:experienceYears IS NULL OR j.experienceYears <= :experienceYears)")
    Page<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("jobType") JobType jobType,
                         @Param("experienceYears") Integer experienceYears,
                         Pageable pageable);
    
}
