package com.avijitmondal.ops.controller;

import com.avijitmondal.ops.dto.OrderResponse;
import com.avijitmondal.ops.dto.UserResponse;
import com.avijitmondal.ops.model.OrderStatus;
import com.avijitmondal.ops.model.User;
import com.avijitmondal.ops.repository.UserRepository;
import com.avijitmondal.ops.service.OrderService;
import com.avijitmondal.ops.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "test@example.com")
    void myOrders_withoutStatusFilter_returnsAllOrders() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");

        UserResponse userResponse = new UserResponse(user.getId(), "Test User", "test@example.com", LocalDateTime.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userService.getUserById(user.getId())).thenReturn(userResponse);
        when(orderService.getAllOrders(eq(user.getId()), eq(null))).thenReturn(Arrays.asList());

        mockMvc.perform(get("/users/me/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-orders"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void myOrders_withStatusFilter_returnsFilteredOrders() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");

        UserResponse userResponse = new UserResponse(user.getId(), "Test User", "test@example.com", LocalDateTime.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userService.getUserById(user.getId())).thenReturn(userResponse);
        when(orderService.getAllOrders(eq(user.getId()), eq(OrderStatus.PENDING))).thenReturn(Arrays.asList());

        mockMvc.perform(get("/users/me/orders").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(view().name("user-orders"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("selectedStatus", "PENDING"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void myOrderDetails_returnsOrderDetails() throws Exception {
        User user = new User();
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");

        UserResponse userResponse = new UserResponse(userId, "Test User", "test@example.com", LocalDateTime.now());
        OrderResponse orderResponse = new OrderResponse(
                orderId, userId, OrderStatus.PENDING, Arrays.asList(), 100.0, null, null
        );

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userService.getUserById(userId)).thenReturn(userResponse);
        when(orderService.getOrderById(userId, orderId)).thenReturn(orderResponse);

        mockMvc.perform(get("/users/me/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(view().name("order-details"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("order"));
    }
}
