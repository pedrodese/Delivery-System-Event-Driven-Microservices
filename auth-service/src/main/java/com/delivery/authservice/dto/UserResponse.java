package com.delivery.authservice.dto;

import com.delivery.authservice.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        Role role,
        String phone,
        Boolean active,
        LocalDateTime createdAt
) {}