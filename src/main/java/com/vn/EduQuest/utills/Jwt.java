package com.vn.EduQuest.utills;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class Jwt {
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long PASSWORD_RESET_EXPIRATION = 300000; // 5 minutes in milliseconds
    private static final String BLACKLISTED_TOKEN_PREFIX = "blacklisted_token:";

    @Autowired
    private RedisService redisService;

    public String generatePasswordResetToken(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + PASSWORD_RESET_EXPIRATION);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("purpose", "PASSWORD_RESET")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims validateToken(String token) {
        // Check if token is blacklisted
        String redisKey = BLACKLISTED_TOKEN_PREFIX + token;
        if (Boolean.TRUE.equals(redisService.hasKey(redisKey))) {
            throw new RuntimeException("Token has been invalidated");
        }

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
