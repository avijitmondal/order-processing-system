package com.avijitmondal.ops.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations including generation, validation, and claims extraction.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        var claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        var username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String generateToken(String username) {
        return createToken(Map.of(), username); // Using Map.of() for immutable empty map
    }

    private String createToken(Map<String, Object> claims, String subject) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(now))
        .expiration(new Date(now + expiration))
        .signWith(getSignKey()) // auto-selects appropriate alg for key
        .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = decodeSecret(secret);
        if (keyBytes.length < 32) { // 32 bytes == 256 bits
            throw new WeakKeyException("JWT secret too short (" + (keyBytes.length * 8) + " bits). Provide >= 256-bit secret. Generate one with: openssl rand -base64 32");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Decodes the configured secret supporting either:
     *  - Base64 (default JJWT expectation)
     *  - Hex (common user choice for static secrets)
     *  - Raw UTF-8 (fallback) if decoding fails
     */
    private byte[] decodeSecret(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("JWT secret must not be empty");
        }
        String trimmed = value.trim();
        // Hex detection: even length, only 0-9A-Fa-f
        if (trimmed.matches("(?i)^[0-9a-f]+$") && trimmed.length() % 2 == 0) {
            return hexToBytes(trimmed);
        }
        try {
            return Decoders.BASE64.decode(trimmed);
        } catch (IllegalArgumentException e) {
            // Fallback to raw bytes
            return trimmed.getBytes(StandardCharsets.UTF_8);
        }
    }

    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
