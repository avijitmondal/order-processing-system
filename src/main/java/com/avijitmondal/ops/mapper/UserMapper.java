package com.avijitmondal.ops.mapper;

import com.avijitmondal.ops.dto.UserResponse;
import com.avijitmondal.ops.model.User;

/**
 * Mapper utility for converting User entities to DTOs.
 * Using static factory methods following modern Java best practices.
 */
public final class UserMapper {
    
    private UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Maps a User entity to UserResponse DTO.
     *
     * @param user the user entity
     * @return the user response DTO
     */
    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
