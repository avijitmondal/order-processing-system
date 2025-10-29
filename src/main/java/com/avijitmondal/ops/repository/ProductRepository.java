package com.avijitmondal.ops.repository;

import com.avijitmondal.ops.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCategory(String category);
    List<Product> findByStockGreaterThan(Integer stock);
    Optional<Product> findByNameIgnoreCase(String name);
    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);
    Page<Product> findByCategoryAndStockGreaterThan(String category, Integer stock, Pageable pageable);
}
