package com.capg.springboot.dto;

import com.capg.springboot.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;   // JWT — valid 15 minutes
    private String refreshToken;  // UUID — valid 7 days, stored in DB
    private Long userId;
    private String name;
    private String email;
    private Role role;
    // password is NEVER included in the response
}
