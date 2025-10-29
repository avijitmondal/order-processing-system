package com.avijitmondal.ops.mapper;

import com.avijitmondal.ops.dto.OrderItemResponse;
import com.avijitmondal.ops.dto.OrderResponse;
import com.avijitmondal.ops.model.Order;
import com.avijitmondal.ops.model.OrderItem;
import com.avijitmondal.ops.model.OrderStatus;
import com.avijitmondal.ops.model.Product;
import com.avijitmondal.ops.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private Order order;
    private OrderItem orderItem;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("password");

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Test Product");
        product.setPrice(100.0);

        orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(100.0);

        order = new Order();
        order.setId(UUID.randomUUID());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(200.0);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);
        order.setItems(items);
        orderItem.setOrder(order);
    }

    @Test
    void toResponse_convertsOrderToOrderResponse() {
        OrderResponse response = OrderMapper.toResponse(order);

        assertNotNull(response);
        assertEquals(order.getId(), response.id());
        assertEquals(user.getId(), response.userId());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(200.0, response.totalAmount());
        assertNotNull(response.items());
        assertEquals(1, response.items().size());
        assertEquals(order.getCreatedAt(), response.createdAt());
        assertEquals(order.getUpdatedAt(), response.updatedAt());
    }

    @Test
    void toItemResponse_convertsOrderItemToOrderItemResponse() {
        OrderItemResponse response = OrderMapper.toItemResponse(orderItem);

        assertNotNull(response);
        assertEquals(orderItem.getId(), response.id());
        assertEquals("Test Product", response.productName());
        assertEquals(2, response.quantity());
        assertEquals(100.0, response.price());
    }

    @Test
    void toResponse_withMultipleItems_convertsAll() {
        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("Product 2");
        product2.setPrice(50.0);

        OrderItem item2 = new OrderItem();
        item2.setId(UUID.randomUUID());
        item2.setProduct(product2);
        item2.setQuantity(3);
        item2.setPrice(50.0);
        item2.setOrder(order);

        order.getItems().add(item2);

        OrderResponse response = OrderMapper.toResponse(order);

        assertNotNull(response);
        assertEquals(2, response.items().size());
    }
}
