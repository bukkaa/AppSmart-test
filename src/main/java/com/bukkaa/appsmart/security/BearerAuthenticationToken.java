package com.bukkaa.appsmart.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class BearerAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;

    public BearerAuthenticationToken(String username) {
        super(null);
        this.username = username;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return username;
    }
}
