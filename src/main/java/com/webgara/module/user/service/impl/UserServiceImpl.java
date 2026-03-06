package com.webgara.module.user.service.impl;

import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.user.dto.ChangePasswordRequest;
import com.webgara.module.user.dto.UpdateProfileRequest;
import com.webgara.module.user.dto.UserProfileResponse;
import com.webgara.module.user.mapper.UserMapper;
import com.webgara.module.user.model.User;
import com.webgara.module.user.repository.UserRepository;
import com.webgara.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userMapper.updateUserFromRequest(request, user);

        if (request.getAddress() != null) {
            user.setAddress(User.Address.builder()
                    .street(request.getAddress().getStreet())
                    .ward(request.getAddress().getWard())
                    .district(request.getAddress().getDistrict())
                    .city(request.getAddress().getCity())
                    .build());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toProfileResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
