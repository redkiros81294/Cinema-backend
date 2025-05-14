package com.cinema.service;

import com.cinema.dto.auth.SignupRequest;
import com.cinema.dto.auth.LoginRequest;
import com.cinema.dto.user.UserResponse;
import com.cinema.dto.user.UserUpdateRequest;
import com.cinema.model.entity.UserRole;
import java.util.List;

public interface UserService {
    // Authentication methods
    UserResponse register(SignupRequest request);
    UserResponse login(LoginRequest request);
    
    // User management methods
    UserResponse getUserByEmail(String email);
    UserResponse getUserByPhone(String phone);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    
    // Validation methods
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    // Admin methods
    List<UserResponse> getAllUsers();
    UserResponse updateUserRole(Long userId, UserRole role);
    UserResponse updateUserStatus(Long userId, boolean active);
    UserResponse deactivateUser(Long userId);
    UserResponse reactivateUser(Long userId);
} 