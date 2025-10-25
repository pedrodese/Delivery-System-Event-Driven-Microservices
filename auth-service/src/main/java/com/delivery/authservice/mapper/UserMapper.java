package com.delivery.authservice.mapper;

import com.delivery.authservice.dto.AuthResponse;
import com.delivery.authservice.dto.RegisterRequest;
import com.delivery.authservice.dto.UserResponse;
import com.delivery.authservice.enums.Role;
import com.delivery.authservice.model.User;

public class UserMapper {

    public static User toEntity(RegisterRequest dto, String encodedPassword) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(encodedPassword);
        user.setRole(dto.role() != null ? dto.role() : Role.CUSTOMER);
        user.setPhone(dto.phone());
        return user;
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getPhone(),
                user.getActive(),
                user.getCreatedAt()
        );
    }

    public static AuthResponse.UserInfo toUserInfo(User user) {
        return new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}