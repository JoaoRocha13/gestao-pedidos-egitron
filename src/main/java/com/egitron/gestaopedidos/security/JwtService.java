package com.egitron.gestaopedidos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expirationMillis}")
    private long expirationMillis;

    private SecretKey key;

    @PostConstruct
    void init() {
        // jjwt 0.11.x: a key HMAC deve ter >= 256 bits para HS256
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            Claims c = parseClaims(token);
            return c.getExpiration() != null && c.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
