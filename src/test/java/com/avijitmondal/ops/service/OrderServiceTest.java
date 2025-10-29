package com.avijitmondal.ops.service;

import com.avijitmondal.ops.dto.CreateOrderRequest;
import com.avijitmondal.ops.dto.OrderItemRequest;
import com.avijitmondal.ops.dto.UpdateOrderStatusRequest;
import com.avijitmondal.ops.exception.InsufficientStockException;
import com.avijitmondal.ops.exception.InvalidOrderStatusException;
import com.avijitmondal.ops.exception.OrderNotFoundException;
import com.avijitmondal.ops.exception.UserNotFoundException;
import com.avijitmondal.ops.model.*;
import com.avijitmondal.ops.repository.OrderRepository;
import com.avijitmondal.ops.repository.ProductRepository;
import com.avijitmondal.ops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product productA;
    private Product productB;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("hashed");
        user.setCreatedAt(LocalDateTime.now());

        productA = new Product();
        productA.setId(UUID.randomUUID());
        productA.setName("Laptop");
        productA.setDescription("High-end laptop");
        productA.setPrice(1500.0);
        productA.setStock(10);
        productA.setCategory("Electronics");

        productB = new Product();
        productB.setId(UUID.randomUUID());
        productB.setName("Mouse");
        productB.setDescription("Wireless mouse");
        productB.setPrice(50.0);
        productB.setStock(5);
        productB.setCategory("Accessories");
    }

    @Test
    void createOrder_success_decrementsStockAndUsesCanonicalPrice() {
        var item1 = new OrderItemRequest("Laptop", 2, 9999.99); // client-supplied price ignored
        var item2 = new OrderItemRequest("Mouse", 1, 0.01);
        var request = new CreateOrderRequest(List.of(item1, item2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findByNameIgnoreCase("Laptop")).thenReturn(Optional.of(productA));
        when(productRepository.findByNameIgnoreCase("Mouse")).thenReturn(Optional.of(productB));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.createOrderForUser(user.getId(), request);

        assertNotNull(response);
        assertEquals(user.getId(), response.userId());
        assertEquals(2, response.items().size());
        assertEquals(8, productA.getStock());
        assertEquals(4, productB.getStock());
        assertEquals(1500.0, response.items().get(0).price());
        assertEquals(50.0, response.items().get(1).price());
        assertEquals(3050.0, response.totalAmount());
    }

    @Test
    void createOrder_insufficientStockThrowsConflict() {
        var item = new OrderItemRequest("Mouse", 50, 50.0);
        var request = new CreateOrderRequest(List.of(item));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findByNameIgnoreCase("Mouse")).thenReturn(Optional.of(productB));

        assertThrows(InsufficientStockException.class, () ->
                orderService.createOrderForUser(user.getId(), request)
        );
        assertEquals(5, productB.getStock());
    }

    @Test
    void createOrder_userNotFound() {
        var item = new OrderItemRequest("Laptop", 1, 1500.0);
        var request = new CreateOrderRequest(List.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> orderService.createOrderForUser(UUID.randomUUID(), request));
    }

    @Test
    void getOrderById_success() {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        var resp = orderService.getOrderById(user.getId(), order.getId());
        assertEquals(order.getId(), resp.id());
    }

    @Test
    void getOrderById_notFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(user.getId(), UUID.randomUUID()));
    }

    @Test
    void updateOrderStatus_success() {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var request = new UpdateOrderStatusRequest(OrderStatus.PROCESSING);
        var resp = orderService.updateOrderStatus(user.getId(), order.getId(), request);
        assertEquals(OrderStatus.PROCESSING, resp.status());
    }

    @Test
    void cancelOrder_invalidStatus() {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setUser(user);
        order.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        assertThrows(InvalidOrderStatusException.class, () -> orderService.cancelOrder(user.getId(), order.getId()));
    }

    @Test
    void cancelOrder_success() {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        orderService.cancelOrder(user.getId(), order.getId());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
