package com.avijitmondal.ops.service;

import com.avijitmondal.ops.dto.UserResponse;
import com.avijitmondal.ops.exception.UserNotFoundException;
import com.avijitmondal.ops.model.User;
import com.avijitmondal.ops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserResponse response = userService.getUserById(user.getId());
        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals("John Doe", response.name());
        assertEquals("john.doe@example.com", response.email());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void testGetUserById_NotFound() {
        UUID missingId = UUID.randomUUID();
        when(userRepository.findById(missingId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(missingId));
        verify(userRepository, times(1)).findById(missingId);
    }
}
