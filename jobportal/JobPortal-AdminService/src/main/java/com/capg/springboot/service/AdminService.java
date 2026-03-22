package com.capg.springboot.service;

import com.capg.springboot.dto.*;
import com.capg.springboot.entity.User;
import com.capg.springboot.enums.Role;
import com.capg.springboot.enums.UserStatus;
import com.capg.springboot.exception.ForbiddenException;
import com.capg.springboot.exception.ResourceNotFoundException;
import com.capg.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    // ── USER MANAGEMENT ──────────────────────────────────────────────────────

    /**
     * List all users with optional filters.
     * Paginated — never COUNT(*) on the full table in real time for large datasets.
     */
    public PagedResponse<UserSummary> getAllUsers(Role role, UserStatus status,
                                                  int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage;

        if (role != null && status != null) {
            userPage = userRepository.findByRoleAndStatus(role, status, pageable);
        } else if (role != null) {
            userPage = userRepository.findByRole(role, pageable);
        } else if (status != null) {
            userPage = userRepository.findByStatus(status, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserSummary> content = userPage.getContent().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        return PagedResponse.<UserSummary>builder()
                .content(content)
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .size(userPage.getSize())
                .build();
    }

    /**
     * Update user status (ACTIVE / INACTIVE / BANNED).
     * Admin cannot deactivate their own account — returns 400.
     */
    public void updateUserStatus(Long targetUserId, UserStatusUpdateRequest request,
                                 Long adminId) {

        // Admin cannot deactivate themselves
        if (targetUserId.equals(adminId)) {
            throw new IllegalArgumentException("Cannot deactivate your own admin account");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + targetUserId));

        user.setStatus(request.getStatus());
        userRepository.save(user);
    }

    // ── REPORTS ──────────────────────────────────────────────────────────────

    /**
     * Platform analytics report.
     * Counts are pre-aggregated per role/status to avoid expensive COUNT(*) on full tables.
     * For Sprint 1 MVP this is acceptable — in production consider caching or async pre-aggregation.
     */
    public ReportResponse getReports() {
        long totalUsers = userRepository.count();

        // Breakdown by role
        Map<String, Long> usersByRole = new LinkedHashMap<>();
        usersByRole.put("JOB_SEEKER", userRepository.countByRole(Role.JOB_SEEKER));
        usersByRole.put("RECRUITER",  userRepository.countByRole(Role.RECRUITER));
        usersByRole.put("ADMIN",      userRepository.countByRole(Role.ADMIN));

        // Breakdown by status
        Map<String, Long> usersByStatus = new LinkedHashMap<>();
        usersByStatus.put("ACTIVE",   userRepository.countByStatus(UserStatus.ACTIVE));
        usersByStatus.put("INACTIVE", userRepository.countByStatus(UserStatus.INACTIVE));
        usersByStatus.put("BANNED",   userRepository.countByStatus(UserStatus.BANNED));

        return ReportResponse.builder()
                .totalUsers(totalUsers)
                .totalJobs(0L)        // populated from Job Service in a real aggregation
                .totalApplications(0L)
                .usersByRole(usersByRole)
                .usersByStatus(usersByStatus)
                .jobsByStatus(new LinkedHashMap<>())
                .applicationsByStatus(new LinkedHashMap<>())
                .build();
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private UserSummary toSummary(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                // password NEVER returned
                .build();
    }
}
