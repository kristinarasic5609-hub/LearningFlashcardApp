package com.flashlearn.app.service;

import com.flashlearn.app.exception.AppException;
import com.flashlearn.app.model.dto.*;
import com.flashlearn.app.model.entity.User;
import com.flashlearn.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(409, "Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        AuthUserDto authUser = toAuthUser(user);
        return new AuthResponse(authUser, jwtService.signToken(authUser));
    }

    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(401, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(401, "Invalid email or password");
        }

        AuthUserDto authUser = toAuthUser(user);
        return new AuthResponse(authUser, jwtService.signToken(authUser));
    }

    /**
     * Logout is client-side for stateless JWT; this method documents the contract.
     */
    public void logoutUser() {
        // Token invalidation is handled on the client by removing the stored token.
    }

    @Transactional
    public AuthResponse updateProfile(AuthUserDto currentUser, UpdateProfileRequest request) {
        User user = userRepository.findById(currentUser.id())
                .orElseThrow(() -> new AppException(404, "User not found"));

        if (!user.getEmail().equals(request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(user.getId())) {
                    throw new AppException(409, "Email is already registered");
                }
            });
            user.setEmail(request.getEmail());
        }

        user.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        AuthUserDto authUser = toAuthUser(user);
        return new AuthResponse(authUser, jwtService.signToken(authUser));
    }

    public static AuthUserDto toAuthUser(User user) {
        return new AuthUserDto(user.getId(), user.getEmail(), user.getUsername());
    }
}
