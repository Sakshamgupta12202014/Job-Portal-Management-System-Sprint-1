package com.jobportal.job.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.job.dto.JobRequest;
import com.jobportal.job.dto.JobResponse;
import com.jobportal.job.dto.PagedResponse;
import com.jobportal.job.enums.JobType;
import com.jobportal.job.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
	
	private final JobService jobService;

	public JobController(JobService jobService) {
	    this.jobService = jobService;
	}
	
	
	@PostMapping
	public ResponseEntity<JobResponse> createJob(
	        @Valid @RequestBody JobRequest request,
	        @RequestHeader("X-User-Id") Long userId,
	        @RequestHeader("X-User-Role") String role) {

	    JobResponse response = jobService.createJob(request, userId , role);
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	

	@GetMapping
	public ResponseEntity<PagedResponse> getAllJobs(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    PagedResponse response = jobService.getAllJobs(page, size);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@GetMapping("/{id}")
	public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {

	    JobResponse response = jobService.getJobById(id);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@GetMapping("/search")
	public ResponseEntity<PagedResponse> searchJobs(
	        @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) String location,
	        @RequestParam(required = false) JobType jobType,
	        @RequestParam(required = false) Integer experienceYears,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    PagedResponse response = jobService.searchJobs(keyword, location, jobType, experienceYears, page, size);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@PutMapping("/{id}")
	public ResponseEntity<JobResponse> updateJob(
	        @PathVariable Long id,
	        @Valid @RequestBody JobRequest request,
	        @RequestHeader("X-User-Id") Long userId,
	        @RequestHeader("X-User-Role") String role) {

	    JobResponse response = jobService.updateJob(id, request, userId , role);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteJob(
	        @PathVariable Long id,
	        @RequestHeader("X-User-Id") Long userId,
	        @RequestHeader("X-User-Role") String role) {

	    jobService.deleteJob(id, userId, role);
	    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	
	@GetMapping("/my-jobs")
	public ResponseEntity<List<JobResponse>> getMyJobs(
	        @RequestHeader("X-User-Id") Long userId,
	        @RequestHeader("X-User-Role") String role) {

	    List<JobResponse> response = jobService.getMyJobs(userId , role);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
