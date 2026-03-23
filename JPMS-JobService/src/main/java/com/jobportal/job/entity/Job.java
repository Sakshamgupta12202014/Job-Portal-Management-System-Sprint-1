package com.jobportal.job.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.jobportal.job.enums.JobStatus;
import com.jobportal.job.enums.JobType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 200)
    private String title;
 
    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;
 
    @Column(nullable = false, length = 150)
    private String location;
 
    @Column(name = "salary_min", precision = 12, scale = 2)
    private BigDecimal salaryMin;
 
    @Column(name = "salary_max", precision = 12, scale = 2)
    private BigDecimal salaryMax;
 
    @Column(name = "experience_years")
    private Integer experienceYears;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;
 
    @Column(name = "skills_required")
    private String skillsRequired;
 
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.ACTIVE;
 
    @Column(name = "deadline")
    private LocalDate deadline;
 
    @Column(name = "posted_by", nullable = false)
    private Long postedBy;
 
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    
    public Job() {
    	
    }

	public Job(Long id, String title, String companyName, String location, BigDecimal salaryMin, BigDecimal salaryMax,
			Integer experienceYears, JobType jobType, String skillsRequired, String description, JobStatus status,
			LocalDate deadline, Long postedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.title = title;
		this.companyName = companyName;
		this.location = location;
		this.salaryMin = salaryMin;
		this.salaryMax = salaryMax;
		this.experienceYears = experienceYears;
		this.jobType = jobType;
		this.skillsRequired = skillsRequired;
		this.description = description;
		this.status = status;
		this.deadline = deadline;
		this.postedBy = postedBy;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}

	public Long getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(Long postedBy) {
		this.postedBy = postedBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
}
