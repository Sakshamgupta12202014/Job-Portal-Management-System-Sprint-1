package com.jobportal.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.auth.dao.UserRepository;
import com.jobportal.auth.dto.AuthResponse;
import com.jobportal.auth.dto.LoginRequest;
import com.jobportal.auth.dto.RegisterRequest;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.enums.UserStatus;
import com.jobportal.auth.exception.ResourceNotFoundException;
import com.jobportal.auth.exception.UserAlreadyExistsException;
import com.jobportal.auth.security.JwtUtil;

@Service
public class AuthService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	JwtUtil jwtUtil;

	
	
	public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use: " + request.getEmail());
        }
 
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());

        userRepository.save(user);


        return new AuthResponse("Registration successful. Please login.");
    }	
	
	
	public AuthResponse login(LoginRequest request) {
	    User user = userRepository.findByEmail(request.getEmail()).orElse(null);

	    if (user == null) {
	        throw new IllegalArgumentException("Invalid credentials");
	    }

	    if (user.getStatus() == UserStatus.BANNED) {
	        throw new IllegalArgumentException("Account suspended");
	    }

	    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	        throw new IllegalArgumentException("Invalid credentials");
	    }

	    String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
	    String refreshToken = jwtUtil.generateRefreshToken();

	    user.setRefreshToken(refreshToken);
	    userRepository.save(user);

	    return new AuthResponse(accessToken, refreshToken, user.getRole().name(), user.getId(), user.getName(), user.getEmail());
	}
	

	public AuthResponse refresh(String refreshToken) {
	    User user = userRepository.findByRefreshToken(refreshToken).orElse(null);

	    if (user == null) {
	        throw new ResourceNotFoundException("Invalid or expired refresh token");
	    }

	    if (user.getStatus() == UserStatus.BANNED) {
	        throw new IllegalArgumentException("Account suspended");
	    }

	    String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
	    String newRefreshToken = jwtUtil.generateRefreshToken();

	    user.setRefreshToken(newRefreshToken);
	    userRepository.save(user);

	    return new AuthResponse(newAccessToken, newRefreshToken, user.getRole().name(), user.getId(), user.getName(), user.getEmail());
	}
	

	public void logout(String refreshToken) {
	    User user = userRepository.findByRefreshToken(refreshToken).orElse(null);

	    if (user == null) {
	        throw new ResourceNotFoundException("Invalid refresh token");
	    }

	    user.setRefreshToken(null);
	    userRepository.save(user);
	}
	
}
