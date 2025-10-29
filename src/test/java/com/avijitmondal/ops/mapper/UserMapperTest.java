package com.avijitmondal.ops.mapper;

import com.avijitmondal.ops.dto.UserResponse;
import com.avijitmondal.ops.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void toResponse_convertsUserToUserResponse() {
        UserResponse response = UserMapper.toResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals("John Doe", response.name());
        assertEquals("john@example.com", response.email());
        assertEquals(user.getCreatedAt(), response.createdAt());
    }
}
