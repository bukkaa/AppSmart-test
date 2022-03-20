package com.bukkaa.appsmart.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = JwtTokenService.shrinkToken(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (StringUtils.hasText(token) && tokenService.validateToken(token)) {
            String username = tokenService.extractUsername(token);

            AbstractAuthenticationToken authentication = new BearerAuthenticationToken(username);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext newSecurityContext = SecurityContextHolder.createEmptyContext();
            newSecurityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(newSecurityContext);
        }

        filterChain.doFilter(request, response);
    }
}
