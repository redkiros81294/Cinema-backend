package com.cinema.service;

import com.cinema.dto.auth.AuthResponse;
import com.cinema.dto.auth.LoginRequest;
import com.cinema.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String token);
} 