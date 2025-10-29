package com.avijitmondal.ops.controller;

import com.avijitmondal.ops.dto.CreateOrderRequest;
import com.avijitmondal.ops.dto.OrderItemRequest;
import com.avijitmondal.ops.dto.UpdateOrderStatusRequest;
import com.avijitmondal.ops.model.*;
import com.avijitmondal.ops.repository.OrderRepository;
import com.avijitmondal.ops.repository.ProductRepository;
import com.avijitmondal.ops.repository.UserRepository;
import com.avijitmondal.ops.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("ordertest@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setName("Order Tester");
        user = userRepository.save(user);

        token = jwtUtil.generateToken(user.getEmail());

        product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(100.0);
        product.setStock(50);
        product.setCategory("Electronics");
        product = productRepository.save(product);
    }

    @Test
    void createOrder_success() throws Exception {
        OrderItemRequest item = new OrderItemRequest("Test Product", 2, 100.0);
        CreateOrderRequest request = new CreateOrderRequest(List.of(item));

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(200.0));
    }

    @Test
    void createOrder_insufficientStock() throws Exception {
        OrderItemRequest item = new OrderItemRequest("Test Product", 100, 100.0);
        CreateOrderRequest request = new CreateOrderRequest(List.of(item));

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getOrderById_success() throws Exception {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(100.0);
        order = orderRepository.save(order);

        mockMvc.perform(get("/api/orders/" + order.getId())
                        .header("Authorization", "Bearer " + token)
                        .param("userId", user.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrderById_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/orders/" + randomId)
                        .header("Authorization", "Bearer " + token)
                        .param("userId", user.getId().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOrders_success() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void getAllOrders_withStatusFilter() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "Bearer " + token)
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void updateOrderStatus_success() throws Exception {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(100.0);
        order = orderRepository.save(order);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.PROCESSING);

        mockMvc.perform(patch("/api/orders/" + order.getId() + "/status")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", user.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void cancelOrder_success() throws Exception {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(100.0);
        order = orderRepository.save(order);

        mockMvc.perform(delete("/api/orders/" + order.getId())
                        .header("Authorization", "Bearer " + token)
                        .param("userId", user.getId().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelOrder_invalidStatus() throws Exception {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PROCESSING);
        order.setTotalAmount(100.0);
        order = orderRepository.save(order);

        mockMvc.perform(delete("/api/orders/" + order.getId())
                        .header("Authorization", "Bearer " + token)
                        .param("userId", user.getId().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_unauthorized() throws Exception {
        OrderItemRequest item = new OrderItemRequest("Test Product", 2, 100.0);
        CreateOrderRequest request = new CreateOrderRequest(List.of(item));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
