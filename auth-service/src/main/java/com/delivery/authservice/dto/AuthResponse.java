package com.delivery.authservice.dto;

public record AuthResponse(
        String token,
        String type,
        Long expiresIn,
        UserInfo user
) {
}