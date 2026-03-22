package com.jobportal.client;

import com.jobportal.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthServiceClient {

    @GetMapping("/api/internal/users")
    List<UserResponse> getAllUsers();

    @DeleteMapping("/api/internal/users/{id}")
    void deleteUser(@PathVariable Long id);
}
