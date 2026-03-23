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

    // ─── User Operations ─────────────────────────────────────────────

    public List<UserResponse> getAllUsers() {
        return authServiceClient.getAllUsers();
    }

    public void deleteUser(Long id, String adminId) {
        authServiceClient.deleteUser(id);
        auditLogRepository.save(new AuditLog("DELETE_USER", "admin:" + adminId, "Deleted user ID: " + id));
    }

    public void banUser(Long id, String adminId) {
        authServiceClient.banUser(id);
        auditLogRepository.save(new AuditLog("BAN_USER", "admin:" + adminId, "Banned user ID: " + id));
    }

    public void unbanUser(Long id, String adminId) {
        authServiceClient.unbanUser(id);
        auditLogRepository.save(new AuditLog("UNBAN_USER", "admin:" + adminId, "Unbanned user ID: " + id));
    }

    // ─── Job Operations ──────────────────────────────────────────────

    public List<JobResponse> getAllJobs() {
        return adminJobClient.getAllJobs();
    }

    public void deleteJob(Long id, String adminId) {
        adminJobClient.deleteJob(id);
        auditLogRepository.save(new AuditLog("DELETE_JOB", "admin:" + adminId, "Deleted job ID: " + id));
    }

    // ─── Platform Report ─────────────────────────────────────────────

    public PlatformReport getReport() {
        List<UserResponse> users = authServiceClient.getAllUsers();
        List<JobResponse> jobs = adminJobClient.getAllJobs();
        ApplicationStats stats = adminAppClient.getStats();

        PlatformReport report = new PlatformReport(users.size(), jobs.size(), stats);
        report.setUsers(users);
        report.setJobs(jobs);
        return report;
    }

    // ─── Audit Logs ──────────────────────────────────────────────────

    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll();
    }
}
