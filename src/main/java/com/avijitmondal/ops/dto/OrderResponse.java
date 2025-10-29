package com.avijitmondal.ops.dto;

import com.avijitmondal.ops.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for order information.
 * Using Java Record for immutability.
 *
 * @param id the order's unique identifier
 * @param userId the user's unique identifier who placed the order
 * @param status the current status of the order
 * @param items list of order items
 * @param totalAmount the total amount of the order
 * @param createdAt timestamp when the order was created
 * @param updatedAt timestamp when the order was last updated
 */
public record OrderResponse(
        UUID id,
        UUID userId,
        OrderStatus status,
        List<OrderItemResponse> items,
        Double totalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
