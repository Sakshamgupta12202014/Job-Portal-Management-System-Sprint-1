package com.capg.springboot.service;

import com.capg.springboot.dto.AuthResponse;
import com.capg.springboot.dto.LoginRequest;
import com.capg.springboot.dto.RefreshTokenRequest;
import com.capg.springboot.dto.RegisterRequest;
import com.capg.springboot.entity.User;
import com.capg.springboot.enums.UserStatus;
import com.capg.springboot.exception.ResourceNotFoundException;
import com.capg.springboot.exception.UserAlreadyExistsException;
import com.capg.springboot.repository.UserRepository;
import com.capg.springboot.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user.
     * - Rejects duplicate emails with 409
     * - BCrypt hashes the password before saving — never store plain text
     * - Returns both access token (15 min) and refresh token (7 days)
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        String refreshToken = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .role(request.getRole())
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .refreshToken(refreshToken)
                .build();

        User saved = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(saved.getId(), saved.getRole().name());

        return buildAuthResponse(saved, accessToken, refreshToken);
    }

    /**
     * Login.
     * - Returns generic "Invalid credentials" for both wrong email AND wrong password
     *   (security best practice — do not reveal which field is wrong)
     * - BANNED users get 403 even with correct credentials
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // BCryptPasswordEncoder.matches() — never compare plain strings
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account suspended");
        }

        String refreshToken = UUID.randomUUID().toString();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    /**
     * Issue a new access token using the refresh token.
     * Refresh token is a UUID stored in the users table.
     * Returns 404 if token is not found (e.g. after logout).
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired refresh token"));

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());

        return buildAuthResponse(user, newAccessToken, user.getRefreshToken());
    }

    /**
     * Logout — clears refresh token from DB.
     * If the same refresh token is used again, findByRefreshToken returns empty → 404.
     */
    public void logout(RefreshTokenRequest request) {
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid refresh token"));
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                // password is NEVER returned
                .build();
    }
}
