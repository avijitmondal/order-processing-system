package com.avijitmondal.ops.controller;

import com.avijitmondal.ops.model.Product;
import com.avijitmondal.ops.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.avijitmondal.ops.dto.PageResponse;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product catalog endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    @Operation(
        summary = "Get all products",
        description = "Retrieve all products with optional category filtering and pagination"
    )
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
        content = @Content(schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<PageResponse<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "name") @Parameter(description = "Sort field") String sort,
            @RequestParam(defaultValue = "asc") @Parameter(description = "Sort direction (asc/desc)") String direction,
            @RequestParam(required = false) @Parameter(description = "Filter by category") String category) {
        Sort.Direction dir = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        logger.debug("Get products (paged) request - page={}, size={}, sort={}, dir={}, category={}", page, size, sort, dir, category);
        var pageResult = category == null
                ? productRepository.findByStockGreaterThan(0, pageable)
                : productRepository.findByCategoryAndStockGreaterThan(category, 0, pageable);
        logger.info("Products page retrieved - page={}, size={}, totalElements={}", pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements());
        return ResponseEntity.ok(PageResponse.of(pageResult));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get product by ID",
        description = "Retrieve product details by product ID"
    )
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
        content = @Content(schema = @Schema(implementation = Product.class)))
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<Product> getProductById(
            @PathVariable @Parameter(description = "Product ID") UUID id) {
        logger.debug("Get product by ID request - ProductId: {}", id);
        
        return productRepository.findById(id)
                .map(product -> {
                    logger.debug("Product found - ProductId: {}, Name: {}", 
                        id, product.getName());
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    logger.warn("Product not found - ProductId: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/category/{category}")
    @Operation(
        summary = "Get products by category",
        description = "Retrieve all products in a specific category with pagination"
    )
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
        content = @Content(schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<PageResponse<Product>> getProductsByCategory(
            @PathVariable @Parameter(description = "Product category") String category,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "name") @Parameter(description = "Sort field") String sort,
            @RequestParam(defaultValue = "asc") @Parameter(description = "Sort direction (asc/desc)") String direction) {
        logger.debug("Get products by category request - Category: {}", category);
        Sort.Direction dir = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        var pageResult = productRepository.findByCategoryAndStockGreaterThan(category, 0, pageable);
        logger.info("Products page by category - Category: {}, page={}, size={}, totalElements={}", category, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements());
        return ResponseEntity.ok(PageResponse.of(pageResult));
    }
}
