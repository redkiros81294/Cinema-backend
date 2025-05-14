package com.cinema.service.impl;

import com.cinema.dto.auth.AuthResponse;
import com.cinema.dto.auth.LoginRequest;
import com.cinema.dto.auth.SignupRequest;
import com.cinema.exception.BusinessException;
import com.cinema.exception.ResourceNotFoundException;
import com.cinema.model.entity.User;
import com.cinema.model.entity.UserRole;
import com.cinema.repository.UserRepository;
import com.cinema.security.JwtTokenProvider;
import com.cinema.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    // In-memory token blacklist - in production, use Redis or a database
    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhone())) {
            throw new BusinessException("Phone number already registered", "PHONE_EXISTS");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered", "EMAIL_EXISTS");
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateAccessToken(user.getId().toString(), user.getRole().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        return new AuthResponse(token, refreshToken, "Bearer", user.getId(), 
            user.getName(), user.getPhoneNumber(), user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateAccessToken(user.getId().toString(), user.getRole().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        return new AuthResponse(token, refreshToken, "Bearer", user.getId(), 
            user.getName(), user.getPhoneNumber(), user.getEmail());
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token", "INVALID_REFRESH_TOKEN");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(Long.parseLong(userId))
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String token = jwtTokenProvider.generateAccessToken(user.getId().toString(), user.getRole().toString());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());

        return new AuthResponse(token, newRefreshToken, "Bearer", user.getId(), 
            user.getName(), user.getPhoneNumber(), user.getEmail());
    }

    @Override
    public void logout(String token) {
        // Add token to blacklist with expiration time
        long expirationTime = jwtTokenProvider.getExpirationDateFromToken(token).getTime();
        tokenBlacklist.put(token, expirationTime);
        
        // Clean up expired tokens (optional)
        long currentTime = System.currentTimeMillis();
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }
} 