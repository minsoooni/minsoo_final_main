package com.spring.app.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String createAccessToken(String memberId, Integer type, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(memberId)
                .claim("type", type)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .subject(memberId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getMemberId(String token) {
        return getClaims(token).getSubject();
    }

    public Integer getType(String token) {
        Object type = getClaims(token).get("type");
        if (type instanceof Integer) {
            return (Integer) type;
        }
        if (type instanceof Number) {
            return ((Number) type).intValue();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return (List<String>) getClaims(token).get("roles");
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (ExpiredJwtException e) {
            System.out.println("==================================================");
            System.out.println("[JWT EXPIRED]");
            System.out.println("subject : " + e.getClaims().getSubject());
            System.out.println("expired : " + e.getClaims().getExpiration());
            System.out.println("token   : " + token);
            System.out.println("==================================================");
            return false;

        } catch (MalformedJwtException e) {
            System.out.println("[JWT INVALID] malformed token");
            return false;

        } catch (UnsupportedJwtException e) {
            System.out.println("[JWT INVALID] unsupported token");
            return false;

        } catch (Exception e) {
            System.out.println("[JWT INVALID] " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }
}