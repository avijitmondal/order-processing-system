package com.avijitmondal.ops.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for user information.
 * Using Java Record for immutability.
 *
 * @param id the user's unique identifier
 * @param name the user's name
 * @param email the user's email
 * @param createdAt timestamp when the user was created
 */
public record UserResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt
) {
}
