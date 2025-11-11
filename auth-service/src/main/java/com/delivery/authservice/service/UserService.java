package com.delivery.authservice.service;

import com.delivery.authservice.dto.UserResponse;
import com.delivery.authservice.exception.ResourceNotFoundException;
import com.delivery.authservice.mapper.UserMapper;
import com.delivery.authservice.model.User;
import com.delivery.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND_MSG = "User not found";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }

    public UserResponse findById(UUID id) {
        User user = findUserByIdOrThrow(id);
        return UserMapper.toUserResponse(user);
    }

    public UserResponse findByUsername(String username) {
        User user = findUserByUsernameOrThrow(username);
        return UserMapper.toUserResponse(user);
    }

    public void deactivate(UUID id) {
        User user = findUserByIdOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public void activate(UUID id) {
        User user = findUserByIdOrThrow(id);
        user.setActive(true);
        userRepository.save(user);
    }

    private User findUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG));
    }

    private User findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG));
    }
}