package com.cinema.dto.user;

import com.cinema.model.entity.UserRole;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;
} 