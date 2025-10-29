package com.avijitmondal.ops.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set the secret and expiration using reflection since @Value annotations won't work in unit tests
        ReflectionTestUtils.setField(jwtUtil, "secret", "dGhpc19pc19hX3Rlc3Rfc2VjcmV0X2tleV90aGF0X2lzX2xvbmdfZW5vdWdoX2Zvcl9obWFjX3NoYTI1Ng==");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hour
        userDetails = new User("test@example.com", "password", new ArrayList<>());
    }

    @Test
    void generateToken_createsValidToken() {
        String token = jwtUtil.generateToken("test@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtil.generateToken("test@example.com");

        String username = jwtUtil.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String token = jwtUtil.generateToken("test@example.com");

        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void validateToken_wrongUsername_returnsFalse() {
        String token = jwtUtil.generateToken("other@example.com");

        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void extractExpiration_returnsValidDate() {
        String token = jwtUtil.generateToken("test@example.com");

        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void extractClaim_returnsCorrectClaim() {
        String token = jwtUtil.generateToken("test@example.com");

        String subject = jwtUtil.extractClaim(token, Claims::getSubject);

        assertEquals("test@example.com", subject);
    }
}
