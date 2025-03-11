package com.roshan.user.security;

import io.jsonwebtoken.*;
import com.roshan.user.model.User;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final String SECRET_KEY = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";  // Change this in production

    public String generateToken(User user) {
        logger.debug("Generating token for user: {}", user.getUsername());
        String token = Jwts.builder()
                .setClaims(new HashMap<String, Object>())
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token valid for 30 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
        logger.debug("Generated token: {}", token);
        return token;
    }

    public String validateToken(String token) {
        try {
            logger.debug("Validating token: {}", token);
            String username = extractClaim(token, Claims::getSubject);
            Date expiration = extractClaim(token, Claims::getExpiration);
            
            if (expiration.before(new Date())) {
                logger.debug("Token has expired");
                return null;
            }
            
            logger.debug("Token is valid for user: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return null; // Invalid token
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        logger.debug("Extracting claims from token");
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername()) && !extractExpiration(token).before(new Date()));
        logger.debug("Token validity check for user {}: {}", username, isValid);
        return isValid;
    }

    // Get the signing key for JWT token
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
