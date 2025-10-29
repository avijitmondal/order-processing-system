package com.avijitmondal.ops.controller;

import com.avijitmondal.ops.dto.CreateOrderRequest;
import com.avijitmondal.ops.dto.OrderResponse;
import com.avijitmondal.ops.dto.UpdateOrderStatusRequest;
import com.avijitmondal.ops.model.OrderStatus;
import com.avijitmondal.ops.model.User;
import com.avijitmondal.ops.repository.UserRepository;
import com.avijitmondal.ops.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.avijitmondal.ops.dto.PageResponse;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    /**
     * Create a new order
     */
    @PostMapping
    @Operation(
        summary = "Create a new order",
        description = "Create a new order for the authenticated user with specified products and quantities"
    )
    @ApiResponse(responseCode = "201", description = "Order created successfully",
        content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid order data")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "409", description = "Insufficient stock for requested products")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        
        logger.info("Create order request - Email: {}, Items count: {}", 
            email, request.items().size());
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        OrderResponse response = orderService.createOrderForUser(user.getId(), request);
        
        logger.info("Order created successfully - OrderId: {}, UserId: {}, Total: ${}", 
            response.id(), user.getId(), response.totalAmount());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get order by ID for a specific user
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get order by ID",
        description = "Retrieve order details by order ID for a specific user"
    )
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
        content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestParam @Parameter(description = "User ID") UUID userId,
            @PathVariable @Parameter(description = "Order ID") UUID id) {
        
        logger.debug("Get order by ID request - OrderId: {}, UserId: {}", 
            id, userId);
        
        OrderResponse response = orderService.getOrderById(userId, id);
        
        logger.debug("Order retrieved - OrderId: {}, Status: {}", 
            id, response.status());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all orders for a user, optionally filtered by status
     */
    @GetMapping
    @Operation(
        summary = "Get all orders",
        description = "Retrieve all orders for the authenticated user with optional status filtering and pagination"
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
        content = @Content(schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
        @RequestParam(required = false) @Parameter(description = "Filter by order status") OrderStatus status,
        @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
        @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size,
        @RequestParam(defaultValue = "createdAt") @Parameter(description = "Sort field") String sort,
        @RequestParam(defaultValue = "desc") @Parameter(description = "Sort direction (asc/desc)") String direction,
        Authentication authentication) {
    String email = authentication.getName();
    logger.info("Get orders (paged) request - Email: {}, Status filter: {}, page={}, size={}, sort={}, direction={}",
        email, status != null ? status : "ALL", page, size, sort, direction);
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));
    Sort.Direction dir = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
    var pageResult = orderService.getAllOrdersPaged(user.getId(), status, pageable);
    logger.info("Orders page retrieved - UserId: {}, page={}, size={}, totalElements={}",
        user.getId(), pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements());
    return ResponseEntity.ok(PageResponse.of(pageResult));
    }

    /**
     * Update order status for a specific user
     */
    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update order status",
        description = "Update the status of an existing order"
    )
    @ApiResponse(responseCode = "200", description = "Order status updated successfully",
        content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid status transition")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @RequestParam @Parameter(description = "User ID") UUID userId,
            @PathVariable @Parameter(description = "Order ID") UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        
        logger.info("Update order status request - OrderId: {}, UserId: {}, New status: {}", 
            id, userId, request.status());
        
        OrderResponse response = orderService.updateOrderStatus(userId, id, request);
        
        logger.info("Order status updated - OrderId: {}, Status: {}", 
            id, response.status());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an order (only if status is PENDING) for a specific user
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Cancel order",
        description = "Cancel an order (only if status is PENDING)"
    )
    @ApiResponse(responseCode = "204", description = "Order cancelled successfully")
    @ApiResponse(responseCode = "400", description = "Order cannot be cancelled (invalid status)")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<Void> cancelOrder(
            @RequestParam @Parameter(description = "User ID") UUID userId,
            @PathVariable @Parameter(description = "Order ID") UUID id) {
        
        logger.info("Cancel order request - OrderId: {}, UserId: {}", 
            id, userId);
        
        orderService.cancelOrder(userId, id);
        
        logger.info("Order cancelled successfully - OrderId: {}, UserId: {}", 
            id, userId);
        
        return ResponseEntity.noContent().build();
    }
}
