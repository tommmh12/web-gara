package com.webgara.module.user.service;

import com.webgara.module.user.dto.ChangePasswordRequest;
import com.webgara.module.user.dto.UpdateProfileRequest;
import com.webgara.module.user.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getUserProfile(String userId);
    UserProfileResponse updateProfile(String userId, UpdateProfileRequest request);
    void changePassword(String userId, ChangePasswordRequest request);
}
