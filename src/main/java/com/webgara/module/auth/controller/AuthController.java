package com.webgara.module.auth.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.module.auth.dto.AuthResponse;
import com.webgara.module.auth.dto.LoginRequest;
import com.webgara.module.auth.dto.RefreshTokenRequest;
import com.webgara.module.auth.dto.RegisterRequest;
import com.webgara.module.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            // Get userId from authenticated user - using email as identifier to find user
            // This is temporary until we get user from SecurityContext
            authService.logout(email);
            return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }
}
