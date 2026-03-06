package com.webgara.module.user.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.module.user.dto.ChangePasswordRequest;
import com.webgara.module.user.dto.UpdateProfileRequest;
import com.webgara.module.user.dto.UserProfileResponse;
import com.webgara.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile(Authentication authentication) {
        String userId = extractUserIdFromAuthentication(authentication);
        UserProfileResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        String userId = extractUserIdFromAuthentication(authentication);
        UserProfileResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        String userId = extractUserIdFromAuthentication(authentication);
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    private String extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            // In a real implementation, you would query the user repository by email
            // For now, returning the username as a placeholder
            return username;
        }
        return null;
    }
}
