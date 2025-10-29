package com.avijitmondal.ops.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for order items.
 * Using Java Record for immutability and conciseness.
 *
 * @param productName the name of the product
 * @param quantity the quantity ordered (minimum 1)
 * @param price the price per unit (must be positive)
 */
public record OrderItemRequest(
        @NotBlank(message = "Product name is required")
        String productName,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        Double price
) {
}
