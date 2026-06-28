package com.flashlearn.app.service;

import com.flashlearn.app.exception.AppException;
import com.flashlearn.app.model.dto.*;
import com.flashlearn.app.model.entity.User;
import com.flashlearn.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(409, "Email is already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        AuthUserDto authUser = toAuthUser(user);
        return new AuthResponse(authUser, jwtService.signToken(authUser));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(401, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(401, "Invalid email or password");
        }

        AuthUserDto authUser = toAuthUser(user);
        return new AuthResponse(authUser, jwtService.signToken(authUser));
    }

    public static AuthUserDto toAuthUser(User user) {
        return new AuthUserDto(user.getId(), user.getEmail(), user.getUsername(), user.getRole());
    }
}
