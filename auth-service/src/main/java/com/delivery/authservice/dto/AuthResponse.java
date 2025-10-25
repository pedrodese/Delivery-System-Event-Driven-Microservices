package com.delivery.authservice.dto;

import com.delivery.authservice.enums.Role;

import java.util.UUID;

public record AuthResponse(
        String token,
        String type,
        Long expiresIn,
        UserInfo user
) {
    public record UserInfo(
            UUID id,
            String username,
            String email,
            Role role
    ) {}
}