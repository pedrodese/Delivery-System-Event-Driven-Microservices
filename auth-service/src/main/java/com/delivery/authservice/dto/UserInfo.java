package com.delivery.authservice.dto;

import com.delivery.authservice.enums.Role;
import com.delivery.authservice.model.User;

import java.util.UUID;

public record UserInfo(
        UUID id,
        String username,
        String email,
        Role role
) {
    public UserInfo(User entity) {
        this (
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getRole()
        );
    }
}
