package com.avijitmondal.ops.repository;

import com.avijitmondal.ops.model.Order;
import com.avijitmondal.ops.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findAllByOrderByCreatedAtDesc();
    
    List<Order> findByUserId(UUID userId);
    
    List<Order> findByUserIdAndStatus(UUID userId, OrderStatus status);
    
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Page<Order> findByUserId(UUID userId, Pageable pageable);
    Page<Order> findByUserIdAndStatus(UUID userId, OrderStatus status, Pageable pageable);
    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
