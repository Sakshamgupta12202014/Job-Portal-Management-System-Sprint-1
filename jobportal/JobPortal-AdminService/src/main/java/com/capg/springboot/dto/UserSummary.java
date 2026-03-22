package com.capg.springboot.dto;

import com.capg.springboot.enums.Role;
import com.capg.springboot.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String phone;
    private UserStatus status;
    private LocalDateTime createdAt;
    // password is NEVER included
}
