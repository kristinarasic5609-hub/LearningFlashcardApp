package com.flashlearn.app.model.dto;

public record AuthResponse(AuthUserDto user, String token) {
}
