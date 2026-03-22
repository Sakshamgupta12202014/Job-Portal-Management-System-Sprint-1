package com.capg.springboot.controller;

import com.capg.springboot.dto.AuthResponse;
import com.capg.springboot.dto.LoginRequest;
import com.capg.springboot.dto.RefreshTokenRequest;
import com.capg.springboot.dto.RegisterRequest;
import com.capg.springboot.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registration, login, token refresh and logout")
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register — public, no JWT required
    @PostMapping("/register")
    @Operation(summary = "Register a new user (JOB_SEEKER, RECRUITER, or ADMIN)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // POST /api/auth/login — public, no JWT required
    @PostMapping("/login")
    @Operation(summary = "Login with email and password — returns JWT access + refresh token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/refresh — public, uses refresh token instead of JWT
    @PostMapping("/refresh")
    @Operation(summary = "Get a new access token using the refresh token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    // POST /api/auth/logout — clears refresh token in DB
    @PostMapping("/logout")
    @Operation(summary = "Logout — invalidates the refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}
