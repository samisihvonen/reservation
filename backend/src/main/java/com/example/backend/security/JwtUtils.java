// ============================================
// FIXED: JwtUtils - JWT Token Generation & Validation
// ============================================
// File: src/main/java/com/example/backend/security/JwtUtils.java

package com.example.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret:your-secret-key-change-this-in-production-at-least-32-characters}")
    private String secret;

    @Value("${jwt.expiration:86400000}")  // 24 hours in milliseconds
    private long expiration;

    private JwtParser jwtParser;

    private SecretKey getSigningKey() {
        String finalSecret = secret;
        if (secret.length() < 32) {
            finalSecret = secret + "a".repeat(32 - secret.length());
        }
        return Keys.hmacShaKeyFor(finalSecret.getBytes(StandardCharsets.UTF_8));
    }

    private JwtParser getJwtParser() {
        if (jwtParser == null) {
            jwtParser = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build();
        }
        return jwtParser;
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expiration)))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return getJwtParser()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = getJwtParser()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.debug("Token expiration check failed: {}", e.getMessage());
            return true;
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            getJwtParser().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("JWT token is malformed: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        try {
            return getJwtParser()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (JwtException e) {
            logger.error("JWT token is invalid: {}", e.getMessage());
        }
        return null;
    }
}