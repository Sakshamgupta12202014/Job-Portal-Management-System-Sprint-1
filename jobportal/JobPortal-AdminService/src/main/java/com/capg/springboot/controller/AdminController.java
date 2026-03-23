package com.capg.springboot.controller;

import com.capg.springboot.dto.PagedResponse;
import com.capg.springboot.dto.ReportResponse;
import com.capg.springboot.dto.UserStatusUpdateRequest;
import com.capg.springboot.dto.UserSummary;
import com.capg.springboot.enums.Role;
import com.capg.springboot.enums.UserStatus;
import com.capg.springboot.feign.JobServiceClient;
import com.capg.springboot.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "User management, job monitoring, and platform reports")
public class AdminController {

    private final AdminService adminService;
    private final JobServiceClient jobServiceClient;

    // ── USER MANAGEMENT ──────────────────────────────────────────────────────

    /**
     * GET /api/admin/users
     * Query params: page, size, role, status
     * Spring Security already enforces ADMIN role via SecurityConfig.
     * 403 returned automatically if non-admin hits this endpoint.
     */
    @GetMapping("/users")
    @Operation(summary = "List all users with optional filters [ADMIN only]")
    public ResponseEntity<PagedResponse<UserSummary>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(adminService.getAllUsers(role, status, page, size));
    }

    /**
     * PATCH /api/admin/users/{id}/status
     * Body: { "status": "BANNED" }
     * Admin cannot deactivate their own account — returns 400.
     */
    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Update user status (ACTIVE/INACTIVE/BANNED) [ADMIN only]")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        adminService.updateUserStatus(id, request, Long.parseLong(adminId));
        return ResponseEntity.ok().build();
    }

    // ── JOB MANAGEMENT (delegated to Job Service via Feign) ──────────────────

    /**
     * GET /api/admin/jobs
     * Returns ALL jobs including DELETED — delegates to Job Service.
     */
    @GetMapping("/jobs")
    @Operation(summary = "Get all jobs including DELETED [ADMIN only]")
    public ResponseEntity<Object> getAllJobs(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Object jobs = jobServiceClient.getAllJobsForAdmin(userId, "ADMIN", page, size);
        return ResponseEntity.ok(jobs);
    }

    /**
     * DELETE /api/admin/jobs/{id}
     * Force soft-delete any job regardless of ownership.
     */
    @DeleteMapping("/jobs/{id}")
    @Operation(summary = "Force delete any job [ADMIN only]")
    public ResponseEntity<Void> deleteJob(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {

        jobServiceClient.deleteJob(id, userId, "ADMIN");
        return ResponseEntity.noContent().build();
    }

    // ── REPORTS ──────────────────────────────────────────────────────────────

    /**
     * GET /api/admin/reports
     * Returns totalUsers, totalJobs, totalApplications, breakdowns by role/status.
     */
    @GetMapping("/reports")
    @Operation(summary = "Platform analytics report [ADMIN only]")
    public ResponseEntity<ReportResponse> getReports() {
        return ResponseEntity.ok(adminService.getReports());
    }
}
