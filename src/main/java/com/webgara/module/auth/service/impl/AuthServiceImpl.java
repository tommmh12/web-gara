package com.webgara.module.auth.service.impl;

import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ConflictException;
import com.webgara.module.auth.dto.AuthResponse;
import com.webgara.module.auth.dto.LoginRequest;
import com.webgara.module.auth.dto.RegisterRequest;
import com.webgara.module.auth.service.AuthService;
import com.webgara.module.user.model.Role;
import com.webgara.module.user.model.User;
import com.webgara.module.user.repository.UserRepository;
import com.webgara.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered");
        }

        // Check for duplicate phone
        if (userRepository.existsByPhone(request.phone())) {
            throw new ConflictException("Phone already registered");
        }

        User user = User.builder()
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(Role.CUSTOMER)
                .isActive(true)
                .build();

        if (request.address() != null) {
            user.setAddress(User.Address.builder()
                    .street(request.address().street())
                    .ward(request.address().ward())
                    .district(request.address().district())
                    .city(request.address().city())
                    .build());
        }

        User savedUser = userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(savedUser.getEmail())
                .password(savedUser.getPassword())
                .authorities("ROLE_" + savedUser.getRole().name())
                .build();

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        savedUser.setRefreshToken(refreshToken);
        userRepository.save(savedUser);

        return buildAuthResponse(accessToken, refreshToken, savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.identifier(),
                            request.password()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseGet(() -> userRepository.findByPhone(userDetails.getUsername()).orElse(null));

            if (user == null) {
                throw new BadRequestException("User not found");
            }

            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            return buildAuthResponse(accessToken, refreshToken, user);
        } catch (Exception e) {
            throw new BadRequestException("Invalid email/phone or password");
        }
    }

    @Override
    public AuthResponse refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        User user = userRepository.findAll().stream()
                .filter(u -> u.getRefreshToken() != null && u.getRefreshToken().equals(refreshToken))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid or expired refresh token"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        if (!jwtService.validateToken(refreshToken, userDetails)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return buildAuthResponse(newAccessToken, newRefreshToken, user);
    }

    @Override
    @Transactional
    public void logout(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.UserInfoDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .fullName(user.getFullName())
                        .avatar(user.getAvatar())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}
