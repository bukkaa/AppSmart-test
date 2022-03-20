package com.bukkaa.appsmart.integration;

import com.bukkaa.appsmart.dto.ProductDto;
import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.entity.Product;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.manager.ProductManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProductsIntegrationTest<T> extends SecurityIntegrationBaseTest<ProductManager> {

    private static Customer CUSTOMER;

    @Autowired
    private CustomerManager customerManager;


    @BeforeEach
    void setUp() {
        super.baseSetUp();
        restTemplate = new TestRestTemplate();

        CUSTOMER = new Customer();
        CUSTOMER.setTitle("Nike");
        CUSTOMER.setDeleted(false);
        CUSTOMER = customerManager.createCustomer(CUSTOMER);
    }


    @Test
    void createProduct_doesntRequireToken() {
        ProductDto dto = ProductDto.builder()
                                    .title("Product")
                                    .description("Description of the product")
                                    .price(BigDecimal.valueOf(800.55))
                                    .isDeleted(false)
                                    .build();

        ResponseEntity<ProductDto> response = restTemplate.postForEntity(prepareCustomerProductsUrl(), dto, ProductDto.class);

        verify(tokenService, never()).validateToken(anyString());
        verify(tokenService, never()).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductDto actual = response.getBody();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getTitle()).isEqualTo(dto.getTitle());
        assertThat(actual.getDescription()).isEqualTo(dto.getDescription());
        assertThat(actual.getPrice()).isEqualTo(dto.getPrice());
    }

    @Test
    void findAllCustomerProducts_doesntRequireToken() {
        int page = 0;
        int size = 5;

        testedApiUrl = prepareCustomerProductsUrl() + "?page=" + page + "&size=" + size;

        IntStream.range(0, size)
                .mapToObj(i -> {
                    Product product = new Product();
                    product.setCustomer(CUSTOMER);
                    product.setTitle("Product #" + i);
                    product.setDescription("Description of the product #" + i);
                    product.setDeleted(true);
                    product.setPrice(BigDecimal.valueOf(500.14 + i));
                    return product;
                })
                .forEach(product -> manager.createProductForCustomer(CUSTOMER.getId().toString(), product));

        ResponseEntity<List> response = restTemplate.getForEntity(testedApiUrl, List.class);

        verify(tokenService, never()).validateToken(anyString());
        verify(tokenService, never()).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List actualList = response.getBody();
        assertThat(actualList).hasSize(size);
    }

    @Test
    void findProduct_doesntRequireToken() {
        Product expected = new Product();
        expected.setCustomer(CUSTOMER);
        expected.setTitle("Product");
        expected.setDescription("Description of the product");
        expected.setDeleted(true);
        expected.setPrice(BigDecimal.valueOf(111.11));
        expected = manager.createProductForCustomer(CUSTOMER.getId().toString(), expected);

        prepareProductsUrl(expected.getId().toString());

        ResponseEntity<ProductDto> response = restTemplate.getForEntity(testedApiUrl, ProductDto.class);

        verify(tokenService, never()).validateToken(anyString());
        verify(tokenService, never()).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductDto actual = response.getBody();
        assertThat(actual.getId()).isEqualTo(expected.getId().toString());
        assertThat(actual.getCreatedAt()).isEqualToIgnoringMillis(expected.getCreatedAt());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
        assertThat(actual.isDeleted()).isEqualTo(expected.isDeleted());
    }

    @Test
    void updateProduct_requiresToken() {
        Product expected = new Product();
        expected.setCustomer(CUSTOMER);
        expected.setTitle("Product");
        expected.setDescription("Description of the product");
        expected.setDeleted(true);
        expected.setPrice(BigDecimal.valueOf(111.11));
        expected = manager.createProductForCustomer(CUSTOMER.getId().toString(), expected);

        UpdateProductDto updateDto = UpdateProductDto.builder()
                                            .price(BigDecimal.valueOf(800.55))
                                            .isDeleted(false)
                                            .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, getTokenUnwrapped("update"));

        ResponseEntity<ProductDto> response = restTemplate.exchange(
                                                    URI.create(prepareProductsUrl(expected.getId().toString())),
                                                    HttpMethod.PUT,
                                                    new HttpEntity<>(updateDto, headers),
                                                    ProductDto.class);

        verify(tokenService, times(1)).validateToken(anyString());
        verify(tokenService, times(1)).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductDto actual = response.getBody();
        assertThat(actual.getId()).isEqualTo(expected.getId().toString());
        assertThat(actual.getCreatedAt()).isEqualToIgnoringMillis(expected.getCreatedAt());
        assertThat(actual.getModifiedAt()).isNotNull();
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(800.55));
        assertThat(actual.isDeleted()).isFalse();
    }

    @Test
    void deleteProduct_requiresToken() {
        Product expected = new Product();
        expected.setCustomer(CUSTOMER);
        expected.setTitle("Product");
        expected.setDescription("Description of the product");
        expected.setDeleted(true);
        expected.setPrice(BigDecimal.valueOf(111.11));
        expected = manager.createProductForCustomer(CUSTOMER.getId().toString(), expected);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, getTokenUnwrapped("update"));

        ResponseEntity<String> response = restTemplate.exchange(
                                                URI.create(prepareProductsUrl(expected.getId().toString())),
                                                HttpMethod.DELETE,
                                                new HttpEntity<>(headers),
                                                String.class);

        verify(tokenService, times(1)).validateToken(anyString());
        verify(tokenService, times(1)).extractUsername(anyString());

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }



    private String prepareProductsUrl(String productId) {
        return testedApiUrl = baseUrl + "/products/" + productId;
    }

    private String prepareCustomerProductsUrl() {
        return testedApiUrl = baseUrl + "/customers/" + CUSTOMER.getId().toString() + "/products";
    }
}
