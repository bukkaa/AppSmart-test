package com.bukkaa.appsmart.integration;

import com.bukkaa.appsmart.dto.CustomerDto;
import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.manager.CustomerManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomersIntegrationTest extends SecurityIntegrationBaseTest<CustomerManager> {

    @BeforeEach
    void setUp() {
        super.baseSetUp();
        testedApiUrl = baseUrl + "/customers";
        restTemplate = new TestRestTemplate();
    }


    @Test
    void createCustomer_doesntRequireToken() {
        CustomerDto dto = CustomerDto.builder()
                .title("BRAND NEW CUSTOMER")
                .isDeleted(false)
                .build();

        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                                                    testedApiUrl,
                                                    dto,
                                                    CustomerDto.class);

        verify(tokenService, never()).validateToken(anyString());
        verify(tokenService, never()).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerDto actual = response.getBody();
        assertThat(actual.getTitle()).isEqualTo(dto.getTitle());
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
    }

    @Test
    void getCustomer_doesntRequireToken() {
        Customer persistedCustomer = new Customer();
        persistedCustomer.setTitle("BRAND NEW CUSTOMER");
        persistedCustomer.setDeleted(false);
        persistedCustomer = manager.createCustomer(persistedCustomer);

        ResponseEntity<CustomerDto> response = restTemplate.getForEntity(
                                                    prepareCustomersUrl(persistedCustomer.getId().toString()),
                                                    CustomerDto.class);

        verify(tokenService, never()).validateToken(anyString());
        verify(tokenService, never()).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerDto actual = response.getBody();
        assertThat(actual.getTitle()).isEqualTo(persistedCustomer.getTitle());
        assertThat(actual.getId()).isEqualTo(persistedCustomer.getId().toString());
        assertThat(actual.getCreatedAt()).isEqualToIgnoringMillis(persistedCustomer.getCreatedAt());
    }

    @SneakyThrows
    @Test
    void updateCustomer_requiresToken() {
        Customer existed = new Customer();
        existed.setTitle("BRAND NEW CUSTOMER");
        existed.setDeleted(false);
        existed = manager.createCustomer(existed);

        UpdateCustomerDto update = UpdateCustomerDto.builder()
                .title("UPDATED CUSTOMER")
                .isDeleted(true)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, getTokenUnwrapped("update"));

        ResponseEntity<CustomerDto> response = restTemplate.exchange(
                                        URI.create(prepareCustomersUrl(existed.getId().toString())),
                                        HttpMethod.PUT,
                                        new HttpEntity<>(update, headers),
                                        CustomerDto.class);

        verify(tokenService, times(1)).validateToken(anyString());
        verify(tokenService, times(1)).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        CustomerDto updatedCustomer = response.getBody();
        assertThat(updatedCustomer.getId()).isEqualTo(existed.getId().toString());
        assertThat(updatedCustomer.getCreatedAt()).isEqualToIgnoringMillis(existed.getCreatedAt());
        assertThat(updatedCustomer.getModifiedAt()).isNotNull();
        assertThat(updatedCustomer.getTitle()).isEqualTo("UPDATED CUSTOMER");
        assertThat(updatedCustomer.isDeleted()).isTrue();
    }

    @SneakyThrows
    @Test
    void deleteCustomer_requiresToken() {
        Customer existed = new Customer();
        existed.setTitle("SUPER CUSTOMER");
        existed.setDeleted(false);
        existed = manager.createCustomer(existed);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, getTokenUnwrapped("delete"));

        ResponseEntity<String> response = restTemplate.exchange(
                                                URI.create(prepareCustomersUrl(existed.getId().toString())),
                                                HttpMethod.DELETE,
                                                new HttpEntity<>(headers),
                                                String.class);

        verify(tokenService, times(1)).validateToken(anyString());
        verify(tokenService, times(1)).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }


    private String prepareCustomersUrl(String customerId) {
        return testedApiUrl + "/" + customerId;
    }
}