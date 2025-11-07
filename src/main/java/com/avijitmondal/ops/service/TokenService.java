package com.avijitmondal.ops.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for managing JWT tokens in Redis cache
 */
@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private static final String TOKEN_PREFIX = "jwt:token:";
    private static final String USER_TOKEN_PREFIX = "jwt:user:";

    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${jwt.expiration}")
    private Long expiration;

    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Store JWT token in Redis with expiration
     * 
     * @param email User's email (username)
     * @param token JWT token
     */
    public void storeToken(String email, String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            String userKey = USER_TOKEN_PREFIX + email;
            
            // Store token with user email as value
            redisTemplate.opsForValue().set(tokenKey, email, expiration, TimeUnit.MILLISECONDS);
            
            // Store user's current token (for quick lookup and invalidation)
            redisTemplate.opsForValue().set(userKey, token, expiration, TimeUnit.MILLISECONDS);
            
            logger.debug("Token stored in Redis for user: {}", email);
        } catch (Exception e) {
            logger.error("Failed to store token in Redis for user: {}", email, e);
            // Don't throw exception - allow application to continue without Redis
        }
    }

    /**
     * Validate if token exists in Redis
     * 
     * @param token JWT token
     * @return true if token exists and is valid
     */
    public boolean validateToken(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            String email = redisTemplate.opsForValue().get(tokenKey);
            return email != null;
        } catch (Exception e) {
            logger.error("Failed to validate token in Redis", e);
            // If Redis is down, fall back to JWT validation only
            return true;
        }
    }

    /**
     * Get user email associated with token
     * 
     * @param token JWT token
     * @return User's email or null if not found
     */
    public String getUserEmail(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            return redisTemplate.opsForValue().get(tokenKey);
        } catch (Exception e) {
            logger.error("Failed to get user email from Redis", e);
            return null;
        }
    }

    /**
     * Invalidate token (logout)
     * 
     * @param token JWT token
     */
    public void invalidateToken(String token) {
        try {
            String tokenKey = TOKEN_PREFIX + token;
            String email = redisTemplate.opsForValue().get(tokenKey);
            
            if (email != null) {
                String userKey = USER_TOKEN_PREFIX + email;
                redisTemplate.delete(tokenKey);
                redisTemplate.delete(userKey);
                logger.info("Token invalidated for user: {}", email);
            }
        } catch (Exception e) {
            logger.error("Failed to invalidate token in Redis", e);
        }
    }

    /**
     * Invalidate all tokens for a user (e.g., password change)
     * 
     * @param email User's email
     */
    public void invalidateUserTokens(String email) {
        try {
            String userKey = USER_TOKEN_PREFIX + email;
            String token = redisTemplate.opsForValue().get(userKey);
            
            if (token != null) {
                String tokenKey = TOKEN_PREFIX + token;
                redisTemplate.delete(tokenKey);
                redisTemplate.delete(userKey);
                logger.info("All tokens invalidated for user: {}", email);
            }
        } catch (Exception e) {
            logger.error("Failed to invalidate user tokens in Redis for user: {}", email, e);
        }
    }

    /**
     * Check if token exists for user
     * 
     * @param email User's email
     * @return true if user has an active token
     */
    public boolean hasActiveToken(String email) {
        try {
            String userKey = USER_TOKEN_PREFIX + email;
            return redisTemplate.hasKey(userKey);
        } catch (Exception e) {
            logger.error("Failed to check active token in Redis for user: {}", email, e);
            return false;
        }
    }
}
