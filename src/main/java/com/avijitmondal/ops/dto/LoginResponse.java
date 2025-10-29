package com.avijitmondal.ops.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Response DTO for successful login.
 * Using Java Record for immutability.
 *
 * @param token the JWT authentication token
 * @param type the token type (always "Bearer")
 * @param userId the user's unique identifier
 * @param name the user's name
 * @param email the user's email
 */
public record LoginResponse(
        String token,
        
        @JsonProperty(defaultValue = "Bearer")
        String type,
        
        UUID userId,
        String name,
        String email
) {
    /**
     * Compact constructor with default value for type.
     */
    public LoginResponse(String token, UUID userId, String name, String email) {
        this(token, "Bearer", userId, name, email);
    }
}
