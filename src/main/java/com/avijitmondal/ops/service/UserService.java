package com.avijitmondal.ops.service;

import com.avijitmondal.ops.dto.UserResponse;
import com.avijitmondal.ops.exception.UserNotFoundException;
import com.avijitmondal.ops.mapper.UserMapper;
import com.avijitmondal.ops.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for user operations.
 * Refactored using modern Java best practices.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList(); // Modern Java 16+ List creation
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}
