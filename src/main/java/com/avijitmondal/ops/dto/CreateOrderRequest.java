package com.avijitmondal.ops.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Request DTO for creating a new order.
 * Using Java Record for immutability.
 *
 * @param items list of order items (must contain at least one item)
 */
public record CreateOrderRequest(
        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemRequest> items
) {
}
