package com.webgara.module.auth.service;

import com.webgara.module.auth.dto.AuthResponse;
import com.webgara.module.auth.dto.LoginRequest;
import com.webgara.module.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshAccessToken(String refreshToken);
    void logout(String userId);
}
