package com.avijitmondal.ops.service;

import com.avijitmondal.ops.dto.*;
import com.avijitmondal.ops.exception.OrderNotFoundException;
import com.avijitmondal.ops.exception.InvalidOrderStatusException;
import com.avijitmondal.ops.exception.UserNotFoundException;
import com.avijitmondal.ops.exception.InsufficientStockException;
import com.avijitmondal.ops.mapper.OrderMapper;
import com.avijitmondal.ops.model.Order;
import com.avijitmondal.ops.model.OrderItem;
import com.avijitmondal.ops.model.OrderStatus;
import com.avijitmondal.ops.model.Product;
import com.avijitmondal.ops.repository.OrderRepository;
import com.avijitmondal.ops.repository.ProductRepository;
import com.avijitmondal.ops.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

/**
 * Service layer for order operations.
 * Refactored using modern Java best practices.
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, 
                       UserRepository userRepository, 
                       ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse createOrderForUser(UUID userId, CreateOrderRequest request) {
        logger.info("Creating order for user - UserId: {}, Items count: {}", 
                   userId, request.items().size());
        
        // Validate user exists using modern Optional pattern
        var user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found - UserId: {}", userId);
                    return new UserNotFoundException("User not found with id: " + userId);
                });

        var order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        // Add items to order using modern stream and method references
        request.items().forEach(itemRequest -> {
            logger.debug("Processing order item - Product: {}, Requested qty: {}", 
                    itemRequest.productName(), itemRequest.quantity());

            var product = findProductByName(itemRequest.productName());

            // Stock validation
            if (product.getStock() < itemRequest.quantity()) {
                logger.warn("Insufficient stock - Product: {}, Available: {}, Requested: {}", 
                        product.getName(), product.getStock(), itemRequest.quantity());
                throw new InsufficientStockException("Insufficient stock for product '" + product.getName() + "'. Available: " + product.getStock() + ", Requested: " + itemRequest.quantity());
            }

            // Decrement stock
            int newStock = product.getStock() - itemRequest.quantity();
            product.setStock(newStock);
            logger.debug("Stock decremented - Product: {}, New stock: {}", product.getName(), newStock);

            var item = createOrderItem(product, itemRequest.quantity());
            order.addItem(item);
        });

        // Calculate total
        order.calculateTotal();

        // Save order
        var savedOrder = orderRepository.save(order);
        
        logger.info("Order created successfully - OrderId: {}, UserId: {}, Total: ${}, Items: {}", 
                   savedOrder.getId(), userId, savedOrder.getTotalAmount(), savedOrder.getItems().size());

        return OrderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID userId, UUID id) {
        logger.debug("Fetching order - OrderId: {}, UserId: {}", id, userId);
        
        var order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found - OrderId: {}", id);
                    return new OrderNotFoundException("Order not found with id: " + id);
                });
        
        // Verify order belongs to user
        validateOrderOwnership(order, userId);
        
        logger.debug("Order retrieved - OrderId: {}, Status: {}", id, order.getStatus());
        
        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(UUID userId, OrderStatus status) {
        logger.debug("Fetching all orders - UserId: {}, Status filter: {}", userId, status);
        
        var orders = status != null 
            ? orderRepository.findByUserIdAndStatus(userId, status)
            : orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        logger.info("Orders retrieved - UserId: {}, Count: {}, Status filter: {}", 
                   userId, orders.size(), status != null ? status : "ALL");
        
        return orders.stream()
                .map(OrderMapper::toResponse)
                .toList(); // Modern Java 16+ List creation
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrdersPaged(UUID userId, OrderStatus status, Pageable pageable) {
        logger.debug("Fetching paged orders - UserId: {}, Status filter: {}, Page: {}", userId, status, pageable.getPageNumber());
        Page<Order> page = status != null
                ? orderRepository.findByUserIdAndStatus(userId, status, pageable)
                : orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        logger.info("Paged orders retrieved - UserId: {}, Page: {}, Size: {}, TotalElements: {}", userId, pageable.getPageNumber(), pageable.getPageSize(), page.getTotalElements());
        return page.map(OrderMapper::toResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(UUID userId, UUID id, UpdateOrderStatusRequest request) {
        logger.info("Updating order status - OrderId: {}, UserId: {}, New status: {}", 
                   id, userId, request.status());
        
        var order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found for status update - OrderId: {}", id);
                    return new OrderNotFoundException("Order not found with id: " + id);
                });
        
        validateOrderOwnership(order, userId);

        var oldStatus = order.getStatus();
        order.setStatus(request.status());
        var updatedOrder = orderRepository.save(order);
        
        logger.info("Order status updated - OrderId: {}, Old status: {}, New status: {}", 
                   id, oldStatus, request.status());

        return OrderMapper.toResponse(updatedOrder);
    }

    @Transactional
    public void cancelOrder(UUID userId, UUID id) {
        logger.info("Cancelling order - OrderId: {}, UserId: {}", id, userId);
        
        var order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found for cancellation - OrderId: {}", id);
                    return new OrderNotFoundException("Order not found with id: " + id);
                });
        
        validateOrderOwnership(order, userId);

        if (order.getStatus() != OrderStatus.PENDING) {
            logger.warn("Cannot cancel order - OrderId: {}, Current status: {}", id, order.getStatus());
            throw new InvalidOrderStatusException(
                    "Only orders with PENDING status can be cancelled. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        logger.info("Order cancelled successfully - OrderId: {}, UserId: {}", id, userId);
    }

    @Transactional
    public int updatePendingOrdersToProcessing() {
        var pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        int count = pendingOrders.size();
        if (count > 0) {
            pendingOrders.forEach(order -> order.setStatus(OrderStatus.PROCESSING));
            orderRepository.saveAll(pendingOrders);
        }
        // Single outcome log line
        logger.info("updatePendingOrdersToProcessing() - updated={} pending orders", count);
        return count;
    }

    // Private helper methods

    private Product findProductByName(String productName) {
        return productRepository.findByNameIgnoreCase(productName)
                .orElseThrow(() -> {
                    logger.error("Product not found - ProductName: {}", productName);
                    return new RuntimeException("Product not found: " + productName);
                });
    }

    private OrderItem createOrderItem(Product product, int quantity) {
        var item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        // Always use canonical product price from DB, ignore client-supplied price for integrity
        item.setPrice(product.getPrice());
        return item;
    }

    private void validateOrderOwnership(Order order, UUID userId) {
        if (!order.getUser().getId().equals(userId)) {
            logger.warn("Unauthorized order access attempt - OrderId: {}, RequestedUserId: {}, ActualUserId: {}", 
                       order.getId(), userId, order.getUser().getId());
            throw new OrderNotFoundException("Order not found with id: " + order.getId() + " for user: " + userId);
        }
    }
}
