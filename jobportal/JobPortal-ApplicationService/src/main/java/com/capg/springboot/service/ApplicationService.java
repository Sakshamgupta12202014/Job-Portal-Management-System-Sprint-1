package com.capg.springboot.service;

import com.capg.springboot.dto.*;
import com.capg.springboot.entity.Application;
import com.capg.springboot.enums.ApplicationStatus;
import com.capg.springboot.exception.*;
import com.capg.springboot.feign.JobServiceClient;
import com.capg.springboot.repository.ApplicationRepository;
import com.capg.springboot.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobServiceClient jobServiceClient;
    private final FileUploadUtil fileUploadUtil;

    // ── APPLY ────────────────────────────────────────────────────────────────

    /**
     * Submit a job application.
     * Rules:
     *  - Job must exist and be ACTIVE (not DELETED/CLOSED)
     *  - Deadline must not have passed
     *  - Seeker cannot apply to the same job twice — 409
     *  - Resume file is required — validated for type and size
     */
    public ApplicationResponse apply(Long jobId, String coverLetter,
                                     MultipartFile resumeFile, Long seekerId) throws IOException {

        // Fetch job from Job Service via Feign
        JobClientResponse job = jobServiceClient.getJobById(jobId,
                String.valueOf(seekerId), "JOB_SEEKER");

        if (job == null || "DELETED".equals(job.getStatus()) || "CLOSED".equals(job.getStatus())) {
            throw new ResourceNotFoundException("Job not found or no longer available");
        }

        // Check deadline
        if (job.getDeadline() != null && !job.getDeadline().isEmpty()) {
            LocalDate deadline = LocalDate.parse(job.getDeadline());
            if (deadline.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Application deadline has passed for this job");
            }
        }

        // Duplicate application check — also enforced by DB unique constraint
        if (applicationRepository.existsByUserIdAndJobId(seekerId, jobId)) {
            throw new DuplicateApplicationException("You have already applied to this job.");
        }

        // Save resume file — validates MIME type and size
        if (resumeFile == null || resumeFile.isEmpty()) {
            throw new IllegalArgumentException("Resume file is required");
        }
        String resumeUrl = fileUploadUtil.saveFile(resumeFile);

        Application application = Application.builder()
                .userId(seekerId)
                .jobId(jobId)
                .resumeUrl(resumeUrl)
                .coverLetter(coverLetter)
                .status(ApplicationStatus.APPLIED)
                .build();

        return toSeekerResponse(applicationRepository.save(application));
    }

    // ── SEEKER: list own applications ────────────────────────────────────────

    public List<ApplicationResponse> getMyApplications(Long seekerId) {
        return applicationRepository.findByUserId(seekerId).stream()
                .map(this::toSeekerResponse)
                .collect(Collectors.toList());
    }

    public ApplicationResponse getApplicationById(Long id, Long seekerId) {
        Application app = applicationRepository.findByIdAndUserId(id, seekerId)
                .orElseThrow(() -> new ForbiddenException(
                        "Application not found or you do not have access to it"));
        return toSeekerResponse(app);
    }

    // ── RECRUITER: view applicants + update status ───────────────────────────

    public List<RecruiterApplicationResponse> getApplicantsForJob(Long jobId, Long recruiterId) {
        // Verify recruiter owns this job
        JobClientResponse job = jobServiceClient.getJobById(jobId,
                String.valueOf(recruiterId), "RECRUITER");
        if (job == null) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }
        if (!job.getPostedBy().equals(recruiterId)) {
            throw new ForbiddenException("You do not own this job listing");
        }

        return applicationRepository.findByJobId(jobId).stream()
                .map(this::toRecruiterResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update application status.
     *
     * Allowed transitions (forward only — REJECTED is final):
     *  APPLIED       → UNDER_REVIEW
     *  UNDER_REVIEW  → SHORTLISTED
     *  UNDER_REVIEW  → REJECTED
     *  SHORTLISTED   → REJECTED
     *
     * Backward transitions throw InvalidStatusTransitionException (400).
     */
    public ApplicationResponse updateStatus(Long applicationId, StatusUpdateRequest request,
                                            Long recruiterId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + applicationId));

        // Verify recruiter owns the job this application belongs to
        JobClientResponse job = jobServiceClient.getJobById(app.getJobId(),
                String.valueOf(recruiterId), "RECRUITER");
        if (job == null || !job.getPostedBy().equals(recruiterId)) {
            throw new ForbiddenException("You do not own the job for this application");
        }

        validateStatusTransition(app.getStatus(), request.getNewStatus());

        app.setStatus(request.getNewStatus());
        if (request.getRecruiterNote() != null) {
            app.setRecruiterNote(request.getRecruiterNote());
        }

        return toSeekerResponse(applicationRepository.save(app));
    }

    // ── STATUS TRANSITION VALIDATOR ──────────────────────────────────────────

    /**
     * Status can only move forward. REJECTED is final.
     * Any backward move throws InvalidStatusTransitionException (400).
     */
    private void validateStatusTransition(ApplicationStatus current, ApplicationStatus next) {
        if (current == ApplicationStatus.REJECTED) {
            throw new InvalidStatusTransitionException(
                    "REJECTED is a final state — no further transitions allowed");
        }

        boolean valid = switch (current) {
            case APPLIED       -> next == ApplicationStatus.UNDER_REVIEW;
            case UNDER_REVIEW  -> next == ApplicationStatus.SHORTLISTED
                                  || next == ApplicationStatus.REJECTED;
            case SHORTLISTED   -> next == ApplicationStatus.REJECTED;
            default            -> false;
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(
                    "Invalid status transition: " + current + " → " + next
                    + ". Status can only move forward.");
        }
    }

    // ── MAPPERS ──────────────────────────────────────────────────────────────

    private ApplicationResponse toSeekerResponse(Application a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .jobId(a.getJobId())
                .resumeUrl(a.getResumeUrl())
                .coverLetter(a.getCoverLetter())
                .status(a.getStatus())
                .appliedAt(a.getAppliedAt())
                .updatedAt(a.getUpdatedAt())
                // recruiterNote intentionally excluded
                .build();
    }

    private RecruiterApplicationResponse toRecruiterResponse(Application a) {
        return RecruiterApplicationResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .jobId(a.getJobId())
                .resumeUrl(a.getResumeUrl())
                .coverLetter(a.getCoverLetter())
                .status(a.getStatus())
                .recruiterNote(a.getRecruiterNote())
                .appliedAt(a.getAppliedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
