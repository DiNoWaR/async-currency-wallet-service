package com.zad.wallet.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import org.springframework.stereotype.Component;


@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    @Value("${jwt.token.expiration.ms}")
    private int jwtExpirationMs;

    private final JwtParser jwtParser;

    public JwtService(@Value("${jwt.secret.key}") String secret) {
        var keyBytes = Decoders.BASE64.decode(secret);
        this.jwtParser = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(keyBytes)).build();
    }

    public String generateToken(String userId) {
        try {
            var keyBytes = Decoders.BASE64.decode(jwtSecret);
            var jwt = Jwts.builder()
                    .setSubject(userId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(SignatureAlgorithm.HS256, keyBytes)
                    .compact();

            return jwt;
        } catch (Exception ex) {
            log.error("error: {}, userId :{}", ex.getMessage(), userId);
            throw ex;
        }
    }

    public boolean validateJwtToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                 SignatureException | IllegalArgumentException ex) {
            log.error("error: {}, token: {}", ex.getMessage(), token);
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            var claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception ex) {
            log.error("error: {}, token: {}", ex.getMessage(), token);
            throw ex;
        }
    }
}
