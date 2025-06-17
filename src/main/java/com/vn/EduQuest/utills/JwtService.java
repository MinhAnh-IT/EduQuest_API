package com.vn.EduQuest.utills;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vn.EduQuest.payload.request.UserForGenerateToken;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtService {

    @Value("${EduQuest.jwt.secret}")
    private String jwtSecret;

    @Value("${EduQuest.jwt.expiration}")
    private long jwtExpiration;

    @Value("${EduQuest.jwt.refresh.expiration}")
    private long jwtRefreshExpiration;

    @Value("${EduQuest.jwt.audience}")
    private String audience;

    @Value("${EduQuest.jwt.issuer}")
    private String issuer;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }

    public String generateAccessToken(UserForGenerateToken userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        return JWT.create()
                .withSubject(Long.toString(userDetails.getId()))
                .withAudience(audience)
                .withIssuedAt(now)
                .withClaim("username", userDetails.getUsername())
                .withIssuer(issuer)
                .withExpiresAt(expiryDate)
                .withClaim("role", "ROLE_" + userDetails.getRole().name())
                .sign(getAlgorithm());
    }

    public String generateRefreshToken(UserForGenerateToken userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpiration);
        return JWT.create()
                .withSubject(Long.toString(userDetails.getId()))
                .withAudience(audience)
                .withIssuedAt(now)
                .withIssuer(issuer)
                .withExpiresAt(expiryDate)
                .withClaim("role", "ROLE_" + userDetails.getRole().name())
                .sign(getAlgorithm());
    }

    public Long getUserIdFromJWT(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm())
                .withIssuer(issuer)
                .build()
                .verify(token);
        return Long.parseLong(decodedJWT.getSubject());
    }
    public String getUsernameFromJWT(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm())
                .withIssuer(issuer)
                .build()
                .verify(token);
        return decodedJWT.getClaim("username").asString();
    }
    public String getRoleFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(getAlgorithm())
                .withIssuer(issuer)
                .build()
                .verify(token);
        return decodedJWT.getClaim("role").asString();
    }

    public boolean validateToken(String authToken) {
        try {
            JWT.require(getAlgorithm())
                    .withIssuer(issuer)
                    .build()
                    .verify(authToken);
            return true;
        } catch (Exception ex) {
            log.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
