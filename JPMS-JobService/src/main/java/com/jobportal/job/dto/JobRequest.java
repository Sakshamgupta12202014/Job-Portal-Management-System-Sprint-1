package com.jobportal.job.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.jobportal.job.enums.JobType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JobRequest {
	
	@NotBlank(message = "Job title is required")
    private String title;
 
    @NotBlank(message = "Company name is required")
    private String companyName;
 
    @NotBlank(message = "Location is required")
    private String location;
 
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private Integer experienceYears;
 
    @NotNull(message = "Job type is required")
    private JobType jobType;
 
    private String skillsRequired;
 
    @NotBlank(message = "Description is required")
    private String description;
 
    private LocalDate deadline;
    
    
    public JobRequest() {
    	
    }


	public JobRequest(@NotBlank(message = "Job title is required") String title,
			@NotBlank(message = "Company name is required") String companyName,
			@NotBlank(message = "Location is required") String location, BigDecimal salaryMin, BigDecimal salaryMax,
			Integer experienceYears, @NotNull(message = "Job type is required") JobType jobType, String skillsRequired,
			@NotBlank(message = "Description is required") String description, LocalDate deadline) {
		super();
		this.title = title;
		this.companyName = companyName;
		this.location = location;
		this.salaryMin = salaryMin;
		this.salaryMax = salaryMax;
		this.experienceYears = experienceYears;
		this.jobType = jobType;
		this.skillsRequired = skillsRequired;
		this.description = description;
		this.deadline = deadline;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public BigDecimal getSalaryMin() {
		return salaryMin;
	}


	public void setSalaryMin(BigDecimal salaryMin) {
		this.salaryMin = salaryMin;
	}


	public BigDecimal getSalaryMax() {
		return salaryMax;
	}


	public void setSalaryMax(BigDecimal salaryMax) {
		this.salaryMax = salaryMax;
	}


	public Integer getExperienceYears() {
		return experienceYears;
	}


	public void setExperienceYears(Integer experienceYears) {
		this.experienceYears = experienceYears;
	}


	public JobType getJobType() {
		return jobType;
	}


	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}


	public String getSkillsRequired() {
		return skillsRequired;
	}


	public void setSkillsRequired(String skillsRequired) {
		this.skillsRequired = skillsRequired;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public LocalDate getDeadline() {
		return deadline;
	}


	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}
    
}
