package com.avijitmondal.ops.service;

import com.avijitmondal.ops.dto.CreateOrderRequest;
import com.avijitmondal.ops.dto.OrderItemRequest;
import com.avijitmondal.ops.dto.OrderResponse;
import com.avijitmondal.ops.exception.OrderNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceAdditionalTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setId(UUID.randomUUID());
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);
        testProduct.setStock(10);

        testOrder = new Order();
        testOrder.setId(UUID.randomUUID());
        testOrder.setUser(testUser);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(200.0);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setOrder(testOrder);
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);
        orderItem.setPrice(100.0);

        testOrder.setItems(Arrays.asList(orderItem));
    }

    @Test
    void getAllOrders_withoutStatus_returnsAllOrders() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId())).thenReturn(orders);

        List<OrderResponse> result = orderService.getAllOrders(testUser.getId(), null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByUserIdOrderByCreatedAtDesc(testUser.getId());
    }

    @Test
    void getAllOrders_withStatus_returnsFilteredOrders() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByUserIdAndStatus(testUser.getId(), OrderStatus.PENDING))
                .thenReturn(orders);

        List<OrderResponse> result = orderService.getAllOrders(testUser.getId(), OrderStatus.PENDING);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findByUserIdAndStatus(testUser.getId(), OrderStatus.PENDING);
    }
}
