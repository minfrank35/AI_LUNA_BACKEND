package com.minfrank.luna.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long tokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    public String createToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
} 