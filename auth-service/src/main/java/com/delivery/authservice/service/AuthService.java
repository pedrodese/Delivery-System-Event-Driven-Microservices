package com.delivery.authservice.service;

import com.delivery.authservice.dto.AuthResponse;
import com.delivery.authservice.dto.LoginRequest;
import com.delivery.authservice.dto.RegisterRequest;
import com.delivery.authservice.exception.BadRequestException;
import com.delivery.authservice.mapper.UserMapper;
import com.delivery.authservice.model.User;
import com.delivery.authservice.repository.UserRepository;
import com.delivery.authservice.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = UserMapper.toEntity(request, encodedPassword);
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole()
        );

        return new AuthResponse(
                token,
                "Bearer",
                jwtUtil.getExpirationTime(),
                UserMapper.toUserInfo(savedUser)
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.getActive()) {
            throw new BadRequestException("User is inactive");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(
                token,
                "Bearer",
                jwtUtil.getExpirationTime(),
                UserMapper.toUserInfo(user)
        );
    }

    public Boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}