package com.avijitmondal.ops.dto;

import java.util.UUID;

/**
 * Response DTO for order items.
 * Using Java Record for immutability.
 *
 * @param id the order item's unique identifier
 * @param productName the name of the product
 * @param quantity the quantity ordered
 * @param price the price per unit
 */
public record OrderItemResponse(
        UUID id,
        String productName,
        Integer quantity,
        Double price
) {
}
