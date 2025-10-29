package com.avijitmondal.ops.dto;

import com.avijitmondal.ops.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating order status.
 * Using Java Record for immutability.
 *
 * @param status the new order status
 */
public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
