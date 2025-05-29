package com.example.ddo_pay.common.util;

import com.example.ddo_pay.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    private long accessTokenValidity = 60 * 60 * 1000L;
    private long refreshTokenValidity = 14 * 24 * 60 * 60 * 1000L;

    public String generateAccessToken(User user) {
        return createToken(user, accessTokenValidity);
    }

    public String generateRefreshToken(User user) {
        return createToken(user, refreshTokenValidity);
    }

    private String createToken(User user, long validityInMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMillis);
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            // 토큰이 올바르지 않거나 만료된 경우 예외 처리
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
        return Long.valueOf(claims.getSubject());
    }
}