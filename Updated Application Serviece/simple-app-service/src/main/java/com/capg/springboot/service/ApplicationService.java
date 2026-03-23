package com.capg.springboot.service;

import com.capg.springboot.dto.ApplicationResponse;
import com.capg.springboot.dto.JobClientResponse;
import com.capg.springboot.dto.RecruiterApplicationResponse;
import com.capg.springboot.dto.StatusUpdateRequest;
import com.capg.springboot.entity.Application;
import com.capg.springboot.enums.ApplicationStatus;
import com.capg.springboot.exception.DuplicateApplicationException;
import com.capg.springboot.exception.ForbiddenException;
import com.capg.springboot.exception.InvalidStatusTransitionException;
import com.capg.springboot.exception.ResourceNotFoundException;
import com.capg.springboot.feign.JobServiceClient;
import com.capg.springboot.repository.ApplicationRepository;
import com.capg.springboot.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// @Service tells Spring that this class holds business logic
// Spring will create one instance of this class and inject it wherever needed

@Service
public class ApplicationService {

    // These are injected by Spring automatically using @Autowired
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobServiceClient jobServiceClient;

    @Autowired
    private FileUploadUtil fileUploadUtil;


    // =========================================================================
    // 1. APPLY FOR A JOB  -  Only JOB_SEEKER can do this
    // =========================================================================

    public ApplicationResponse applyForJob(Long jobId, String coverLetter,
                                           MultipartFile resumeFile, Long seekerId) throws IOException {

        // Step 1: Call the Job Service to get the job details
        // We use Feign client which makes an HTTP call to JOB-SERVICE
        JobClientResponse job = jobServiceClient.getJobById(
                jobId,
                String.valueOf(seekerId),
                "JOB_SEEKER"
        );

        // Step 2: Check if the job exists and is active
        if (job == null) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }

        if ("DELETED".equals(job.getStatus()) || "CLOSED".equals(job.getStatus())) {
            throw new ResourceNotFoundException("This job is no longer accepting applications.");
        }

        // Step 3: Check if the application deadline has passed
        if (job.getDeadline() != null && !job.getDeadline().isEmpty()) {

            LocalDate deadlineDate = LocalDate.parse(job.getDeadline());
            LocalDate today = LocalDate.now();

            if (deadlineDate.isBefore(today)) {
                throw new IllegalArgumentException(
                    "Sorry, the application deadline for this job has passed. " +
                    "Deadline was: " + job.getDeadline()
                );
            }
        }

        // Step 4: Check if this seeker already applied to this job
        boolean alreadyApplied = applicationRepository.existsByUserIdAndJobId(seekerId, jobId);

        if (alreadyApplied) {
            throw new DuplicateApplicationException(
                "You have already applied to this job. " +
                "You cannot apply to the same job more than once."
            );
        }

        // Step 5: Save the resume file to disk
        // FileUploadUtil validates the file type and size
        String resumeUrl = fileUploadUtil.saveFile(resumeFile);

        // Step 6: Create a new Application object and save it to the database
        Application newApplication = new Application();
        newApplication.setUserId(seekerId);
        newApplication.setJobId(jobId);
        newApplication.setResumeUrl(resumeUrl);
        newApplication.setCoverLetter(coverLetter);
        newApplication.setStatus(ApplicationStatus.APPLIED);

        Application savedApplication = applicationRepository.save(newApplication);

        // Step 7: Convert to response DTO and return
        // We do NOT return the entity directly - we use a DTO to control what is visible
        ApplicationResponse response = convertToSeekerResponse(savedApplication);
        return response;
    }


    // =========================================================================
    // 2. GET MY APPLICATIONS  -  Seeker sees only their own applications
    // =========================================================================

    public List<ApplicationResponse> getMyApplications(Long seekerId) {

        // Get all applications from database where user_id matches this seeker
        List<Application> applicationList = applicationRepository.findByUserId(seekerId);

        // Convert each Application entity to ApplicationResponse DTO
        // We use a normal for loop - easy to read and understand
        List<ApplicationResponse> responseList = new ArrayList<>();

        for (int i = 0; i < applicationList.size(); i++) {
            Application application = applicationList.get(i);
            ApplicationResponse response = convertToSeekerResponse(application);
            responseList.add(response);
        }

        return responseList;
    }


    // =========================================================================
    // 3. GET ONE APPLICATION BY ID  -  Seeker can only see their own
    // =========================================================================

    public ApplicationResponse getApplicationById(Long applicationId, Long seekerId) {

        // findByIdAndUserId ensures seeker can ONLY see their own applications
        // If seeker tries to see someone else's application, this returns empty
        Optional<Application> applicationOptional =
                applicationRepository.findByIdAndUserId(applicationId, seekerId);

        if (applicationOptional.isEmpty()) {
            throw new ForbiddenException(
                "Application not found or you do not have permission to view this application."
            );
        }

        Application application = applicationOptional.get();
        return convertToSeekerResponse(application);
    }


    // =========================================================================
    // 4. GET ALL APPLICANTS FOR A JOB  -  Recruiter only, for their own jobs
    // =========================================================================

    public List<RecruiterApplicationResponse> getApplicantsForJob(Long jobId, Long recruiterId) {

        // Step 1: Call Job Service to verify this job exists
        JobClientResponse job = jobServiceClient.getJobById(
                jobId,
                String.valueOf(recruiterId),
                "RECRUITER"
        );

        if (job == null) {
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }

        // Step 2: Check that this recruiter actually owns this job
        // A recruiter should not be able to see applicants of another recruiter's job
        if (!job.getPostedBy().equals(recruiterId)) {
            throw new ForbiddenException(
                "You are not allowed to view applicants for this job. " +
                "You can only view applicants for jobs you have posted."
            );
        }

        // Step 3: Get all applications for this job from the database
        List<Application> applicationList = applicationRepository.findByJobId(jobId);

        // Step 4: Convert each to RecruiterApplicationResponse (includes recruiterNote)
        List<RecruiterApplicationResponse> responseList = new ArrayList<>();

        for (int i = 0; i < applicationList.size(); i++) {
            Application application = applicationList.get(i);
            RecruiterApplicationResponse response = convertToRecruiterResponse(application);
            responseList.add(response);
        }

        return responseList;
    }


    // =========================================================================
    // 5. UPDATE APPLICATION STATUS  -  Recruiter only, for their own jobs
    // =========================================================================

    public ApplicationResponse updateApplicationStatus(Long applicationId,
                                                       StatusUpdateRequest request,
                                                       Long recruiterId) {

        // Step 1: Find the application in the database
        Optional<Application> applicationOptional =
                applicationRepository.findById(applicationId);

        if (applicationOptional.isEmpty()) {
            throw new ResourceNotFoundException(
                "Application not found with id: " + applicationId
            );
        }

        Application application = applicationOptional.get();

        // Step 2: Call Job Service to verify this recruiter owns the job
        JobClientResponse job = jobServiceClient.getJobById(
                application.getJobId(),
                String.valueOf(recruiterId),
                "RECRUITER"
        );

        if (job == null) {
            throw new ResourceNotFoundException(
                "The job for this application was not found."
            );
        }

        // Step 3: Check ownership - recruiter must own this job
        if (!job.getPostedBy().equals(recruiterId)) {
            throw new ForbiddenException(
                "You are not allowed to update this application. " +
                "You can only update applications for jobs you have posted."
            );
        }

        // Step 4: Validate the status transition
        // Status can only move FORWARD - never backward
        validateStatusTransition(application.getStatus(), request.getNewStatus());

        // Step 5: Update the status and optionally the recruiter note
        application.setStatus(request.getNewStatus());

        if (request.getRecruiterNote() != null && !request.getRecruiterNote().isEmpty()) {
            application.setRecruiterNote(request.getRecruiterNote());
        }

        // Step 6: Save the updated application to the database
        Application updatedApplication = applicationRepository.save(application);

        // Step 7: Return the updated application as a seeker response
        // (status change is visible to the seeker, but recruiterNote is NOT)
        return convertToSeekerResponse(updatedApplication);
    }


    // =========================================================================
    // PRIVATE HELPER: Validate Status Transition
    // =========================================================================
    //
    // Allowed transitions:
    //   APPLIED       -> UNDER_REVIEW
    //   UNDER_REVIEW  -> SHORTLISTED
    //   UNDER_REVIEW  -> REJECTED
    //   SHORTLISTED   -> REJECTED
    //
    // NOT allowed:
    //   REJECTED -> anything   (final state)
    //   SHORTLISTED -> APPLIED (no going backward)

    private void validateStatusTransition(ApplicationStatus currentStatus,
                                          ApplicationStatus newStatus) {

        // Rule 1: REJECTED is a final state - nothing can come after it
        if (currentStatus == ApplicationStatus.REJECTED) {
            throw new InvalidStatusTransitionException(
                "This application has already been REJECTED. " +
                "No further status changes are allowed."
            );
        }

        // Rule 2: Check if the specific transition is allowed
        boolean isValidTransition = false;

        if (currentStatus == ApplicationStatus.APPLIED) {
            // From APPLIED we can only go to UNDER_REVIEW
            if (newStatus == ApplicationStatus.UNDER_REVIEW) {
                isValidTransition = true;
            }
        }

        else if (currentStatus == ApplicationStatus.UNDER_REVIEW) {
            // From UNDER_REVIEW we can go to SHORTLISTED or REJECTED
            if (newStatus == ApplicationStatus.SHORTLISTED) {
                isValidTransition = true;
            }
            if (newStatus == ApplicationStatus.REJECTED) {
                isValidTransition = true;
            }
        }

        else if (currentStatus == ApplicationStatus.SHORTLISTED) {
            // From SHORTLISTED we can only go to REJECTED
            if (newStatus == ApplicationStatus.REJECTED) {
                isValidTransition = true;
            }
        }

        // Rule 3: If transition is not valid, throw error
        if (!isValidTransition) {
            throw new InvalidStatusTransitionException(
                "Cannot change status from " + currentStatus + " to " + newStatus + ". " +
                "Status can only move forward. " +
                "Valid transitions are: " +
                "APPLIED -> UNDER_REVIEW, " +
                "UNDER_REVIEW -> SHORTLISTED or REJECTED, " +
                "SHORTLISTED -> REJECTED."
            );
        }
    }


    // =========================================================================
    // PRIVATE HELPER: Convert Application entity to Seeker Response DTO
    // =========================================================================
    // This is called when returning data to the JOB SEEKER
    // recruiterNote is NOT included - seeker cannot see it

    private ApplicationResponse convertToSeekerResponse(Application application) {

        ApplicationResponse response = new ApplicationResponse();

        response.setId(application.getId());
        response.setUserId(application.getUserId());
        response.setJobId(application.getJobId());
        response.setResumeUrl(application.getResumeUrl());
        response.setCoverLetter(application.getCoverLetter());
        response.setStatus(application.getStatus());
        response.setAppliedAt(application.getAppliedAt());
        response.setUpdatedAt(application.getUpdatedAt());

        // Note: recruiterNote is NOT set here - this is intentional
        // Seekers should not see internal recruiter notes

        return response;
    }


    // =========================================================================
    // PRIVATE HELPER: Convert Application entity to Recruiter Response DTO
    // =========================================================================
    // This is called when returning data to the RECRUITER
    // recruiterNote IS included - recruiter can see their own notes

    private RecruiterApplicationResponse convertToRecruiterResponse(Application application) {

        RecruiterApplicationResponse response = new RecruiterApplicationResponse();

        response.setId(application.getId());
        response.setUserId(application.getUserId());
        response.setJobId(application.getJobId());
        response.setResumeUrl(application.getResumeUrl());
        response.setCoverLetter(application.getCoverLetter());
        response.setStatus(application.getStatus());
        response.setRecruiterNote(application.getRecruiterNote()); // included for recruiter
        response.setAppliedAt(application.getAppliedAt());
        response.setUpdatedAt(application.getUpdatedAt());

        return response;
    }

}
