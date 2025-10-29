package com.avijitmondal.ops.mapper;

import com.avijitmondal.ops.dto.OrderItemResponse;
import com.avijitmondal.ops.dto.OrderResponse;
import com.avijitmondal.ops.model.Order;
import com.avijitmondal.ops.model.OrderItem;

/**
 * Mapper utility for converting Order entities to DTOs.
 * Using static factory methods following modern Java best practices.
 */
public final class OrderMapper {
    
    private OrderMapper() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Maps an Order entity to OrderResponse DTO.
     *
     * @param order the order entity
     * @return the order response DTO
     */
    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getItems().stream()
                        .map(OrderMapper::toItemResponse)
                        .toList(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
    
    /**
     * Maps an OrderItem entity to OrderItemResponse DTO.
     *
     * @param item the order item entity
     * @return the order item response DTO
     */
    public static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
