package com.jobportal.service;

import com.jobportal.client.JobServiceClient;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.ApplicationStats;
import com.jobportal.model.Application;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.repository.ApplicationRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobServiceClient jobServiceClient;

    // Verifies job exists via Feign, enforces one-application-per-job rule, then saves
    public ApplicationResponse applyForJob(Long jobId, String applicantEmail, String resumeUrl) {
        try {
            jobServiceClient.getJobById(jobId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Job not found: " + jobId);
        }

        if (applicationRepository.existsByApplicantEmailAndJobId(applicantEmail, jobId)) {
            throw new RuntimeException("Already applied for this job");
        }

        Application app = new Application();
        app.setApplicantEmail(applicantEmail);
        app.setJobId(jobId);
        app.setResumeUrl(resumeUrl);
        app.setStatus(ApplicationStatus.APPLIED);

        Application saved = applicationRepository.save(app);
        return new ApplicationResponse(saved.getId(), "Applied successfully");
    }

    public List<ApplicationResponse> getMyApplications(String applicantEmail) {
        return applicationRepository.findByApplicantEmail(applicantEmail)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ApplicationResponse> getApplicationsForJob(Long jobId) {
        return applicationRepository.findByJobId(jobId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Recruiter updates application status (e.g., SHORTLISTED, REJECTED)
    public ApplicationResponse updateStatus(Long applicationId, String status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found: " + applicationId));
        app.setStatus(ApplicationStatus.valueOf(status));
        Application saved = applicationRepository.save(app);
        return toResponse(saved);
    }

    public ApplicationStats getStats() {
        long total = applicationRepository.count();
        long applied = applicationRepository.countByStatus(ApplicationStatus.APPLIED);
        long underReview = applicationRepository.countByStatus(ApplicationStatus.UNDER_REVIEW);
        long shortlisted = applicationRepository.countByStatus(ApplicationStatus.SHORTLISTED);
        long rejected = applicationRepository.countByStatus(ApplicationStatus.REJECTED);
        return new ApplicationStats(total, applied, underReview, shortlisted, rejected);
    }

    private ApplicationResponse toResponse(Application app) {
        return new ApplicationResponse(
                app.getId(), app.getApplicantEmail(), app.getJobId(),
                app.getResumeUrl(), app.getStatus().name(), app.getAppliedAt()
        );
    }
}
