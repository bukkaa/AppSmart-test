package com.bukkaa.appsmart.security;

import org.springframework.util.StringUtils;

public interface JwtTokenService {

    String generateToken(String username);

    boolean validateToken(String token);

    String extractUsername(String token);

    static String shrinkToken(String bearerToken) {
        return StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer")
                                ? bearerToken.substring(7)
                                : bearerToken;
    }
}
