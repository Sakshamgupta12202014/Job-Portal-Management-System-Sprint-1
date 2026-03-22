package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.model.Job;
import com.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public JobResponse createJob(JobRequest req, String recruiterEmail) {
        Job job = new Job();
        job.setTitle(req.getTitle());
        job.setCompanyName(req.getCompanyName());
        job.setLocation(req.getLocation());
        job.setSalary(req.getSalary());
        job.setExperience(req.getExperience());
        job.setDescription(req.getDescription());
        job.setPostedByEmail(recruiterEmail);
        Job saved = jobRepository.save(job);
        return toResponse(saved);
    }

    public Page<JobResponse> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable).map(this::toResponse);
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        return toResponse(job);
    }

    public Page<JobResponse> search(String keyword, String location, String experience, Pageable pageable) {
        return jobRepository.search(keyword, location, experience, pageable).map(this::toResponse);
    }

    public List<JobResponse> getJobsByRecruiter(String recruiterEmail) {
        return jobRepository.findByPostedByEmail(recruiterEmail)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Only the recruiter who posted the job can update it
    public JobResponse updateJob(Long id, JobRequest req, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        if (!job.getPostedByEmail().equals(recruiterEmail)) {
            throw new RuntimeException("You can only update your own job listings");
        }
        job.setTitle(req.getTitle());
        job.setCompanyName(req.getCompanyName());
        job.setLocation(req.getLocation());
        job.setSalary(req.getSalary());
        job.setExperience(req.getExperience());
        job.setDescription(req.getDescription());
        return toResponse(jobRepository.save(job));
    }

    // Only the recruiter who posted the job can delete it
    public void deleteJob(Long id, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
        if (!job.getPostedByEmail().equals(recruiterEmail)) {
            throw new RuntimeException("You can only delete your own job listings");
        }
        jobRepository.delete(job);
    }

    // Admin-level deletion without ownership check
    public void deleteJobByAdmin(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job not found: " + id);
        }
        jobRepository.deleteById(id);
    }

    public List<JobResponse> getAllJobsList() {
        return jobRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(), job.getTitle(), job.getCompanyName(), job.getLocation(),
                job.getSalary(), job.getExperience(), job.getDescription(),
                job.getPostedByEmail(), job.getCreatedAt()
        );
    }
}
