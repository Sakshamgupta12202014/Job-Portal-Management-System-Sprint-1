package com.capg.springboot.service;

import com.capg.springboot.dto.*;
import com.capg.springboot.entity.Job;
import com.capg.springboot.enums.JobStatus;
import com.capg.springboot.exception.ForbiddenException;
import com.capg.springboot.exception.ResourceNotFoundException;
import com.capg.springboot.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    // ── CREATE ──────────────────────────────────────────────────────────────

    public JobResponse createJob(JobRequest request, Long recruiterId) {
        Job job = Job.builder()
                .title(request.getTitle())
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .experienceYears(request.getExperienceYears())
                .jobType(request.getJobType())
                .skillsRequired(request.getSkillsRequired())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .status(JobStatus.ACTIVE)
                .postedBy(recruiterId) // taken from JWT — not from request body
                .build();

        return toResponse(jobRepository.save(job));
    }

    // ── READ (public) ────────────────────────────────────────────────────────

    public PagedResponse<JobResponse> getAllActiveJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Job> jobPage = jobRepository.findByStatus(JobStatus.ACTIVE, pageable);
        return toPagedResponse(jobPage);
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        // Treat DELETED jobs as not found — same as if they never existed
        if (job.getStatus() == JobStatus.DELETED) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
        return toResponse(job);
    }

    public PagedResponse<JobResponse> searchJobs(JobSearchRequest req) {
        if (req.getSalaryMin() != null && req.getSalaryMax() != null
                && req.getSalaryMin().compareTo(req.getSalaryMax()) > 0) {
            throw new IllegalArgumentException("salaryMin must not be greater than salaryMax");
        }
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(),
                Sort.by("createdAt").descending());
        Page<Job> page = jobRepository.searchJobs(
                req.getKeyword(), req.getLocation(), req.getJobType(),
                req.getExperienceYears(), req.getSalaryMin(), req.getSalaryMax(), pageable);
        return toPagedResponse(page);
    }

    // ── RECRUITER OPERATIONS ─────────────────────────────────────────────────

    public JobResponse updateJob(Long id, JobRequest request, Long recruiterId) {
        Job job = getJobOrThrow(id);
        checkOwnership(job, recruiterId);

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

        return toResponse(jobRepository.save(job));
    }

    /**
     * Soft delete — status = DELETED. The row stays in DB for audit.
     * Active applications against this job are preserved.
     */
    public void deleteJob(Long id, Long recruiterId) {
        Job job = getJobOrThrow(id);
        checkOwnership(job, recruiterId);
        job.setStatus(JobStatus.DELETED);
        jobRepository.save(job);
    }

    public PagedResponse<JobResponse> getMyJobs(Long recruiterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Job> jobPage = jobRepository.findByPostedByAndStatusNot(
                recruiterId, JobStatus.DELETED, pageable);
        return toPagedResponse(jobPage);
    }

    // ── ADMIN OPERATIONS ────────────────────────────────────────────────────

    public PagedResponse<JobResponse> getAllJobsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Job> jobPage = jobRepository.findAll(pageable);
        return toPagedResponse(jobPage);
    }

    public void adminDeleteJob(Long id) {
        Job job = getJobOrThrow(id);
        job.setStatus(JobStatus.DELETED);
        jobRepository.save(job);
    }

    // ── HELPERS ─────────────────────────────────────────────────────────────

    private Job getJobOrThrow(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    /**
     * Ownership check — role-based security alone is not enough.
     * A RECRUITER must own the job to edit/delete it.
     */
    private void checkOwnership(Job job, Long currentUserId) {
        if (!job.getPostedBy().equals(currentUserId)) {
            throw new ForbiddenException("You do not have permission to modify this job");
        }
    }

    public boolean isDeadlinePassed(Job job) {
        return job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now());
    }

    public Job getRawJob(Long id) {
        return getJobOrThrow(id);
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .experienceYears(job.getExperienceYears())
                .jobType(job.getJobType())
                .skillsRequired(job.getSkillsRequired())
                .description(job.getDescription())
                .status(job.getStatus())
                .deadline(job.getDeadline())
                .postedBy(job.getPostedBy())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    private PagedResponse<JobResponse> toPagedResponse(Page<Job> page) {
        List<JobResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PagedResponse.<JobResponse>builder()
                .content(content)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .build();
    }
}
