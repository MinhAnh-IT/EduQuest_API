package com.vn.EduQuest.utills;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class Jwt {
    private final SecretKey key;
    private static final long PASSWORD_RESET_EXPIRATION = 300000; // 5 minutes in milliseconds

    public Jwt() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
