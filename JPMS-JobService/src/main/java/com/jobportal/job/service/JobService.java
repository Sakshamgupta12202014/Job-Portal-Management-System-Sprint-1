package com.jobportal.job.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.jobportal.job.dao.JobRepository;
import com.jobportal.job.dto.JobRequest;
import com.jobportal.job.dto.JobResponse;
import com.jobportal.job.dto.PagedResponse;
import com.jobportal.job.entity.Job;
import com.jobportal.job.enums.JobStatus;
import com.jobportal.job.enums.JobType;
import com.jobportal.job.exception.ForbiddenException;
import com.jobportal.job.exception.ResourceNotFoundException;

@Service
public class JobService {
	
	private final JobRepository jobRepository;

	public JobService(JobRepository jobRepository) {
	    this.jobRepository = jobRepository;
	}
	
	
    public JobResponse createJob(JobRequest request, Long userId , String role) {
        
		if (!role.equals("RECRUITER")) {
	        throw new ForbiddenException("Only recruiters can post jobs");
	    }
		
		if (request.getSalaryMin() != null && request.getSalaryMax() != null) {
            if (request.getSalaryMin().compareTo(request.getSalaryMax()) > 0) {
                throw new IllegalArgumentException("Salary min cannot be greater than salary max");
            }
        }
 
        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setExperienceYears(request.getExperienceYears());
        job.setJobType(request.getJobType());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setDescription(request.getDescription());
        job.setDeadline(request.getDeadline());
        job.setPostedBy(userId);
 
        Job savedJob = jobRepository.save(job);
 
 
        return JobResponse.fromEntity(savedJob);
    }
 
	
	public PagedResponse getAllJobs(int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Job> jobs = jobRepository.findByStatusNot(JobStatus.DELETED, pageable);

	    List<JobResponse> content = new ArrayList<>();
	    for (Job job : jobs.getContent()) {
	        content.add(JobResponse.fromEntity(job));
	    }

	    return new PagedResponse(content, jobs.getNumber(), jobs.getTotalElements(), jobs.getTotalPages());
	}

	
	public PagedResponse searchJobs(String keyword, String location,
	                                 JobType jobType, Integer experienceYears,
	                                 int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Job> jobs = jobRepository.searchJobs(keyword, location, jobType, experienceYears, pageable);

	    List<JobResponse> content = new ArrayList<>();
	    for (Job job : jobs.getContent()) {
	        content.add(JobResponse.fromEntity(job));
	    }

	    return new PagedResponse(content, jobs.getNumber(), jobs.getTotalElements(), jobs.getTotalPages());
	}
 
	
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        if (job == null || job.getStatus() == JobStatus.DELETED) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
        return JobResponse.fromEntity(job);
    }
 
 
    public JobResponse updateJob(Long id, JobRequest request, Long userId , String role) {
    	
    	if (!role.equals("RECRUITER")) {
            throw new ForbiddenException("Only recruiters can update jobs");
        }
    	
    	Job job = jobRepository.findById(id).orElse(null);
        
        if (job == null || job.getStatus() == JobStatus.DELETED) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
 
        if (!job.getPostedBy().equals(userId)) {
            throw new ForbiddenException("You can only update your own job listings");
        }
 
        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setExperienceYears(request.getExperienceYears());
        job.setJobType(request.getJobType());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setDescription(request.getDescription());
        job.setDeadline(request.getDeadline());
 
        return JobResponse.fromEntity(jobRepository.save(job));
    }
 
    
    public void deleteJob(Long id, Long userId, String role) {
        
    	if (!role.equals("RECRUITER") && !role.equals("ADMIN")) {
            throw new ForbiddenException("Only recruiters and admin can delete the jobs");
        }
    	
    	Job job = jobRepository.findById(id).orElse(null);
        
        if (job == null || job.getStatus() == JobStatus.DELETED) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
 
        if (role.equals("RECRUITER") && !job.getPostedBy().equals(userId)) {
            throw new ForbiddenException("You can only delete your own job listings");
        }
 
        job.setStatus(JobStatus.DELETED);
        jobRepository.save(job);
    }
 
    
    public List<JobResponse> getMyJobs(Long userId , String role) {
        
    	if (!role.equals("RECRUITER")) {
            throw new ForbiddenException("Only recruiters can view their jobs");
        }
    	
    	List<Job> jobs = jobRepository.findByPostedByAndStatusNot(userId, JobStatus.DELETED);

        List<JobResponse> result = new ArrayList<>();
        for (Job job : jobs) {
            result.add(JobResponse.fromEntity(job));
        }

        return result;
    }
}
