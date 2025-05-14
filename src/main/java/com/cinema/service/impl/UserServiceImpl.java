package com.cinema.service.impl;

import com.cinema.dto.auth.SignupRequest;
import com.cinema.dto.auth.LoginRequest;
import com.cinema.dto.user.UserResponse;
import com.cinema.dto.user.UserUpdateRequest;
import com.cinema.model.entity.User;
import com.cinema.model.entity.UserRole;
import com.cinema.repository.UserRepository;
import com.cinema.service.UserService;
import com.cinema.exception.BusinessException;
import com.cinema.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(SignupRequest request) {
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered", "EMAIL_EXISTS");
        }
        if (existsByPhone(request.getPhone())) {
            throw new BusinessException("Phone number already registered", "PHONE_EXISTS");
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Invalid email or password", "INVALID_CREDENTIALS"));
            
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password", "INVALID_CREDENTIALS");
        }
        
        if (!user.isActive()) {
            throw new BusinessException("Account is deactivated", "ACCOUNT_DEACTIVATED");
        }
        
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        return mapToResponse(userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByPhone(String phone) {
        return mapToResponse(userRepository.findByPhoneNumber(phone)
            .orElseThrow(() -> new ResourceNotFoundException("User", "phone", phone)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return mapToResponse(userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id)));
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw new BusinessException("Email already registered", "EMAIL_EXISTS");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (existsByPhone(request.getPhoneNumber())) {
                throw new BusinessException("Phone number already registered", "PHONE_EXISTS");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        if (request.getPassword() != null) {
            if (request.getCurrentPassword() == null) {
                throw new BusinessException("Current password is required", "CURRENT_PASSWORD_REQUIRED");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException("Current password is incorrect", "INVALID_CURRENT_PASSWORD");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhoneNumber(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findByDeletedFalse().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUserRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setRole(role);
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setActive(active);
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse deactivateUser(Long userId) {
        return updateUserStatus(userId, false);
    }

    @Override
    public UserResponse reactivateUser(Long userId) {
        return updateUserStatus(userId, true);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .role(user.getRole())
            .active(user.isActive())
            .createdAt(user.getCreatedAt())
            .build();
    }
} 