package com.avijitmondal.ops.controller;

import com.avijitmondal.ops.model.Product;
import com.avijitmondal.ops.model.User;
import com.avijitmondal.ops.repository.ProductRepository;
import com.avijitmondal.ops.repository.UserRepository;
import com.avijitmondal.ops.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("producttest@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setName("Product Tester");
        userRepository.save(user);

        token = jwtUtil.generateToken(user.getEmail());

        // Create test products
        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setDescription("High-end laptop");
        product1.setPrice(1500.0);
        product1.setStock(10);
        product1.setCategory("Electronics");
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Mouse");
        product2.setDescription("Wireless mouse");
        product2.setPrice(50.0);
        product2.setStock(5);
        product2.setCategory("Accessories");
        productRepository.save(product2);
    }

    @Test
    void getAllProducts_success() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getAllProducts_withCategoryFilter() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .param("category", "Electronics")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getProductById_success() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(get("/api/products/" + product.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId().toString()))
                .andExpect(jsonPath("$.name").value(product.getName()));
    }

    @Test
    void getProductById_notFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/products/" + randomId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductsByCategory_success() throws Exception {
        mockMvc.perform(get("/api/products/category/Electronics")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAllProducts_unauthorized() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden());
    }
}
