package com.flashlearn.app.controller;

import com.flashlearn.app.model.dto.AuthResponse;
import com.flashlearn.app.model.dto.LoginRequest;
import com.flashlearn.app.model.dto.RegisterRequest;
import com.flashlearn.app.model.dto.UpdateProfileRequest;
import com.flashlearn.app.security.SecurityUtils;
import com.flashlearn.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        userService.logoutUser();
    }

    @PutMapping("/profile")
    public AuthResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(SecurityUtils.requireCurrentUser(), request);
    }
}
