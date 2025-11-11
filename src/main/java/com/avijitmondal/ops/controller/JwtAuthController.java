package com.avijitmondal.ops.controller;

import com.avijitmondal.ops.dto.LoginRequest;
import com.avijitmondal.ops.dto.LoginResponse;
import com.avijitmondal.ops.dto.RegisterRequest;
import com.avijitmondal.ops.model.User;
import com.avijitmondal.ops.repository.UserRepository;
import com.avijitmondal.ops.security.JwtUtil;
import com.avijitmondal.ops.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class JwtAuthController {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthController.class);

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    
    private final TokenService tokenService;

    public JwtAuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, 
                            UserRepository userRepository, PasswordEncoder passwordEncoder,
                            TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.email());
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );

            User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getEmail());
            
            // Store token in Redis
            tokenService.storeToken(user.getEmail(), token);

            logger.info("User logged in successfully - Email: {}, UserId: {}", 
                user.getEmail(), user.getId());

            LoginResponse loginResponse = new LoginResponse(token, user.getId(), user.getName(), user.getEmail());
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for email: {} - Invalid credentials", loginRequest.email());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            logger.error("Error during login for email: {} - {}", loginRequest.email(), e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Register new user", description = "Create a new user account and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Email already registered")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registration attempt for email: {}, name: {}", registerRequest.email(), registerRequest.name());
        
        try {
            if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
                logger.warn("Registration failed - Email already exists: {}", registerRequest.email());
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email already registered");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            User user = new User();
            user.setEmail(registerRequest.email());
            user.setPassword(passwordEncoder.encode(registerRequest.password()));
            user.setName(registerRequest.name());
            
            user = userRepository.save(user);

            String token = jwtUtil.generateToken(user.getEmail());
            
            // Store token in Redis
            tokenService.storeToken(user.getEmail(), token);
            
            logger.info("User registered successfully - Email: {}, UserId: {}, Name: {}", 
                user.getEmail(), user.getId(), user.getName());

            LoginResponse loginResponse = new LoginResponse(token, user.getId(), user.getName(), user.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
        } catch (Exception e) {
            logger.error("Error during registration for email: {} - {}", registerRequest.email(), e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    @Operation(
        summary = "User logout",
        description = "Logout the authenticated user and invalidate token"
    )
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("Logout request");
        
        // Invalidate token in Redis if provided
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.invalidateToken(token);
            logger.info("User token invalidated");
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "Logged out successfully");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current user",
        description = "Retrieve information about the authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "User information retrieved successfully")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        logger.debug("Get current user request");
        
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthorized access attempt to /me endpoint");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        logger.debug("User info retrieved - Email: {}, UserId: {}", 
            email, user.getId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/hash/{password}")
    @Operation(
        summary = "Generate password hash",
        description = "Generate BCrypt hash for the provided password (utility endpoint)"
    )
    @ApiResponse(responseCode = "200", description = "Password hash generated successfully")
    public ResponseEntity<?> generateHash(
            @PathVariable @Parameter(description = "Password to hash") String password) {
        logger.debug("Password hash generation requested");
        String hash = passwordEncoder.encode(password);
        Map<String, String> response = new HashMap<>();
        response.put("password", password);
        response.put("hash", hash);
        return ResponseEntity.ok(response);
    }
}
