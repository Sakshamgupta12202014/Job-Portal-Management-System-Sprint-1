package com.jobportal.job.dto;

import com.jobportal.job.enums.JobStatus;
import com.jobportal.job.enums.JobType;
import com.jobportal.job.entity.Job;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobResponse {

	private Long id;
	private String title;
	private String companyName;
	private String location;
	private BigDecimal salaryMin;
	private BigDecimal salaryMax;
	private Integer experienceYears;
	private JobType jobType;
	private String skillsRequired;
	private String description;
	private JobStatus status;
	private LocalDate deadline;
	private Long postedBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static JobResponse fromEntity(Job job) {
		JobResponse response = new JobResponse();
		response.id = job.getId();
		response.title = job.getTitle();
		response.companyName = job.getCompanyName();
		response.location = job.getLocation();
		response.salaryMin = job.getSalaryMin();
		response.salaryMax = job.getSalaryMax();
		response.experienceYears = job.getExperienceYears();
		response.jobType = job.getJobType();
		response.skillsRequired = job.getSkillsRequired();
		response.description = job.getDescription();
		response.status = job.getStatus();
		response.deadline = job.getDeadline();
		response.postedBy = job.getPostedBy();
		response.createdAt = job.getCreatedAt();
		response.updatedAt = job.getUpdatedAt();
		return response;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getLocation() {
		return location;
	}

	public BigDecimal getSalaryMin() {
		return salaryMin;
	}

	public BigDecimal getSalaryMax() {
		return salaryMax;
	}

	public Integer getExperienceYears() {
		return experienceYears;
	}

	public JobType getJobType() {
		return jobType;
	}

	public String getSkillsRequired() {
		return skillsRequired;
	}

	public String getDescription() {
		return description;
	}

	public JobStatus getStatus() {
		return status;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public Long getPostedBy() {
		return postedBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

}