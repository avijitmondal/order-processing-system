package com.avijitmondal.ops.service;

import com.avijitmondal.ops.model.Order;
import com.avijitmondal.ops.model.OrderStatus;
import com.avijitmondal.ops.model.User;
import com.avijitmondal.ops.repository.OrderRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServicePaginationTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository; // not used directly here but kept for consistency

    @InjectMocks
    private OrderService orderService;

    private UUID userId;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        order1 = new Order();
        order1.setId(UUID.randomUUID());
        order1.setStatus(OrderStatus.PENDING);
        order1.setCreatedAt(LocalDateTime.now());
        order1.setUpdatedAt(LocalDateTime.now());
        var user = new User();
        user.setId(userId);
        order1.setUser(user);

        order2 = new Order();
        order2.setId(UUID.randomUUID());
        order2.setStatus(OrderStatus.PENDING);
        order2.setCreatedAt(LocalDateTime.now());
        order2.setUpdatedAt(LocalDateTime.now());
        order2.setUser(user);
    }

    @Test
    void getAllOrdersPaged_returnsPageMetadata() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Order> pageData = new PageImpl<>(List.of(order1), pageable, 2);
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)).thenReturn(pageData);

        var resultPage = orderService.getAllOrdersPaged(userId, null, pageable);
        assertEquals(1, resultPage.getContent().size());
        assertEquals(2, resultPage.getTotalElements());
        assertFalse(resultPage.isLast());
        assertEquals(order1.getId(), resultPage.getContent().getFirst().id());
    }

    @Test
    void getAllOrdersPaged_withStatusFilter() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Order> pageData = new PageImpl<>(List.of(order1, order2), pageable, 2);
        when(orderRepository.findByUserIdAndStatus(userId, OrderStatus.PENDING, pageable)).thenReturn(pageData);

        var resultPage = orderService.getAllOrdersPaged(userId, OrderStatus.PENDING, pageable);
        assertEquals(2, resultPage.getContent().size());
        assertEquals(OrderStatus.PENDING, resultPage.getContent().getFirst().status());
    }
}
