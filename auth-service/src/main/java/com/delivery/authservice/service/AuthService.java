package com.delivery.authservice.service;

import com.delivery.authservice.dto.AuthResponse;
import com.delivery.authservice.dto.LoginRequest;
import com.delivery.authservice.dto.RegisterRequest;
import com.delivery.authservice.exception.ResourceNotFoundException;
import com.delivery.authservice.mapper.UserMapper;
import com.delivery.authservice.model.User;
import com.delivery.authservice.repository.UserRepository;
import com.delivery.authservice.util.JwtUtil;
import com.delivery.authservice.validator.AuthValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final AuthValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, AuthValidator validator, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validator.validateUsernameExists(request.username());
        validator.validateEmailExists(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = UserMapper.toEntity(request, encodedPassword);
        User savedUser = userRepository.save(user);

        return buildAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = findUserByUsername(request.username());
        validator.validateUserPassword(request.password(), user.getPassword());
        validator.validateUserIsActive(user.getActive());

        return buildAuthResponse(user);
    }

    public Boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return new AuthResponse(
                token,
                TOKEN_TYPE,
                jwtUtil.getExpirationTime(),
                UserMapper.toUserInfo(user)
        );
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}