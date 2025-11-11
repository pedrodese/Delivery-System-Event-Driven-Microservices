package com.delivery.authservice.validator;

import com.delivery.authservice.exception.BadRequestException;
import com.delivery.authservice.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthValidator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateUsernameExists(String username) {
            if (Boolean.TRUE.equals(userRepository.existsByUsername(username))) {
                throw new BadRequestException("Username already exists");
            }
    }

    public void validateEmailExists(String email) {
            if (Boolean.TRUE.equals(userRepository.existsByEmail(email))) {
                throw new BadRequestException("Email already exists");
            }
    }

    public void validateUserPassword(String requestPassword, String userPassword) {
        if (!passwordEncoder.matches(requestPassword, userPassword)) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    public void validateUserIsActive(Boolean isActive) {
        if (!isActive) {
            throw new BadRequestException("User is inactive");
        }
    }
}
