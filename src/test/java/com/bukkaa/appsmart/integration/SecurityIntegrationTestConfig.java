package com.bukkaa.appsmart.integration;

import com.bukkaa.appsmart.security.JwtTokenService;
import com.bukkaa.appsmart.security.JwtTokenServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.spy;

@TestConfiguration
public class SecurityIntegrationTestConfig {

    @Bean
    public JwtTokenService tokenService() {
        return spy(new JwtTokenServiceImpl());
    }
}
