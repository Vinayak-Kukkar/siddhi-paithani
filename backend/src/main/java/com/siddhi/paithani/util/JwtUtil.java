package com.siddhi.paithani.util;

import com.siddhi.paithani.entity.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Secret Key for HMAC-SHA256 signing (at least 256 bits)
    private static final String SECRET_KEY_STRING = "SiddhiPaithaniPureSilkHandwovenHeritageSecuritySecretKey2026";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // Token validity: 7 days in milliseconds
    private static final long EXPIRATION_TIME = 7L * 24 * 60 * 60 * 1000;

    public String generateToken(Customer customer) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("customerId", customer.getCustomerId());
        claims.put("email", customer.getEmail());
        claims.put("mobile", customer.getMobile());
        claims.put("customerName", customer.getCustomerName());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customer.getEmail() != null ? customer.getEmail() : customer.getMobile())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Long extractCustomerId(String token) {
        Claims claims = extractAllClaims(token);
        if (claims != null && claims.get("customerId") != null) {
            return ((Number) claims.get("customerId")).longValue();
        }
        return null;
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public boolean validateToken(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) {
            return false;
        }
        Date expiration = claims.getExpiration();
        return expiration != null && !expiration.before(new Date());
    }
}
