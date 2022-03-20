package com.bukkaa.appsmart.integration;

import com.bukkaa.appsmart.security.JwtTokenService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@Import(SecurityIntegrationTestConfig.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:test-db",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=test",
        "spring.datasource.password=test",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"})
public abstract class SecurityIntegrationBaseTest<T> {

    protected TestRestTemplate restTemplate;

    protected String baseUrl;
    protected String tokenApiUrl;
    protected String testedApiUrl;

    @LocalServerPort
    protected int port;

    @Value("${appsmart.security.token.secret}")
    private String secretKey;

    @Autowired
    protected JwtTokenService tokenService;

    @Autowired
    protected T manager;


    protected void baseSetUp() {
        baseUrl = "http://localhost:" + port + "/api/v1";
        tokenApiUrl = baseUrl + "/token?username=";
    }


    @Test
    void generateToken() {
        final String username = UUID.randomUUID().toString();

        ResponseEntity<String> response = executeGenerateTokenRequest(username);

        verify(tokenService, never()).validateToken(anyString());
        verify(tokenService, never()).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.hasBody()).isTrue();

        String actualToken = response.getBody();
        String actualUsername = parseUsernameFromToken(actualToken);

        assertThat(actualUsername).isEqualTo(username);
    }

    @Test
    void generateToken_generatesDifferentTokens() {
        final String username1 = "username 1";
        final String username2 = "username 2";

        String token1 = getTokenUnwrapped(username1);
        String token2 = getTokenUnwrapped(username2);

        assertThat(token1).isNotEqualTo(token2);

        assertThat(parseUsernameFromToken(token1)).isEqualTo(username1);
        assertThat(parseUsernameFromToken(token2)).isEqualTo(username2);
    }


    protected ResponseEntity<String> executeGenerateTokenRequest(String username) {
        return restTemplate.exchange(tokenApiUrl + username, HttpMethod.GET, null, String.class);
    }

    protected String getTokenUnwrapped(String username) {
        return executeGenerateTokenRequest(username).getBody();
    }

    protected String parseUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
