package com.bukkaa.appsmart.controller;

import com.bukkaa.appsmart.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/token")
public class TokenController {

    private final JwtTokenService tokenService;

    @GetMapping
    public String generateToken(@RequestParam String username) {
        log.info("generateToken <<< username = '{}'", username);

        String token = tokenService.generateToken(username);

        log.info("generateToken >>> JWT token = [{}]", token);
        return token;
    }
}
