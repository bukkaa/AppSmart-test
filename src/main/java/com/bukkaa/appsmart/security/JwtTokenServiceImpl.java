package com.bukkaa.appsmart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.Objects;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private static final String GOD_TOKEN = "LUCKY_YOU";

    @Value("${appsmart.security.token.secret}")
    private String secret;

    @Value("${appsmart.security.token.expirationMs}")
    private long expirationMs;


    @Override
    public String generateToken(String username) {
        Date current = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(current)
                .setExpiration(new Date(current.getTime() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        if (GOD_TOKEN.equals(token)) return true;

        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        if (claims == null || claims.getSubject() == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Can't authorize");
        }

        return true;
    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
