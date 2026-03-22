package com.jobportal.service;

import com.jobportal.client.AdminAppClient;
import com.jobportal.client.AdminJobClient;
import com.jobportal.client.AuthServiceClient;
import com.jobportal.dto.*;
import com.jobportal.model.AuditLog;
import com.jobportal.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private AdminJobClient adminJobClient;

    @Autowired
    private AdminAppClient adminAppClient;

    @Autowired
    private AuditLogRepository auditLogRepository;

    public List<UserResponse> getAllUsers() {
        return authServiceClient.getAllUsers();
    }

    public void deleteUser(Long id, String adminEmail) {
        authServiceClient.deleteUser(id);
        auditLogRepository.save(new AuditLog("DELETE_USER", adminEmail, "Deleted user ID: " + id));
    }

    public List<JobResponse> getAllJobs() {
        return adminJobClient.getAllJobs();
    }

    public void deleteJob(Long id, String adminEmail) {
        adminJobClient.deleteJob(id);
        auditLogRepository.save(new AuditLog("DELETE_JOB", adminEmail, "Deleted job ID: " + id));
    }

    // Aggregates data from all services into a single platform report
    public PlatformReport getReport() {
        List<UserResponse> users = authServiceClient.getAllUsers();
        List<JobResponse> jobs = adminJobClient.getAllJobs();
        ApplicationStats stats = adminAppClient.getStats();

        PlatformReport report = new PlatformReport(users.size(), jobs.size(), stats);
        report.setUsers(users);
        report.setJobs(jobs);
        return report;
    }
}
