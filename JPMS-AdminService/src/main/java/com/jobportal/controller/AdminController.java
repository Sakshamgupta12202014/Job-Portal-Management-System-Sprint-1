package com.jobportal.controller;

import com.jobportal.dto.JobResponse;
import com.jobportal.dto.PlatformReport;
import com.jobportal.dto.UserResponse;
import com.jobportal.exception.AccessDeniedException;
import com.jobportal.model.AuditLog;
import com.jobportal.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ─── Helper: enforce ADMIN role ───────────────────────────────────
    private void assertAdmin(String role) {
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new AccessDeniedException("Access denied. ADMIN role required.");
        }
    }

    // ─── User Management ─────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String adminId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        adminService.deleteUser(id, adminId);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<Map<String, String>> banUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String adminId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        adminService.banUser(id, adminId);
        return ResponseEntity.ok(Map.of("message", "User banned successfully"));
    }

    @PutMapping("/users/{id}/unban")
    public ResponseEntity<Map<String, String>> unbanUser(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String adminId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        adminService.unbanUser(id, adminId);
        return ResponseEntity.ok(Map.of("message", "User unbanned successfully"));
    }

    // ─── Job Management ──────────────────────────────────────────────

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getAllJobs(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        return ResponseEntity.ok(adminService.getAllJobs());
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Map<String, String>> deleteJob(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String adminId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        adminService.deleteJob(id, adminId);
        return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
    }

    // ─── Reports ─────────────────────────────────────────────────────

    @GetMapping("/reports")
    public ResponseEntity<PlatformReport> getReports(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        return ResponseEntity.ok(adminService.getReport());
    }

    // ─── Audit Logs ──────────────────────────────────────────────────

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        assertAdmin(role);
        return ResponseEntity.ok(adminService.getAuditLogs());
    }
}