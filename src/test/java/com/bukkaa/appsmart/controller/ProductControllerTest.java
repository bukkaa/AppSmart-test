package com.bukkaa.appsmart.controller;

import com.bukkaa.appsmart.dto.ProductDto;
import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.entity.Product;
import com.bukkaa.appsmart.manager.ProductManager;
import com.bukkaa.appsmart.mapper.ProductMapper;
import com.bukkaa.appsmart.mapper.ProductMapperImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ProductControllerTest {

    private ProductManager manager;
    private ProductMapper mapper;

    private ProductController controller;

    @BeforeEach
    void setUp() {
        manager = mock(ProductManager.class);
        mapper = spy(ProductMapperImpl.class);
        controller = new ProductController(manager, mapper);
    }


    @Test
    void createProductForCustomer_positive() {
        String customerId = "ID";
        Product expected = new Product();
        expected.setId(UUID.randomUUID());
        expected.setTitle("Air Max");
        expected.setCreatedAt(Timestamp.from(Instant.now()));
        expected.setDescription("Nike, Air Max model");
        expected.setPrice(new BigDecimal("800.55"));
        expected.setDeleted(false);
        when(manager.createProductForCustomer(eq(customerId), any(Product.class))).thenReturn(expected);

        ProductDto dto = ProductDto.builder()
                .title("Air Max")
                .isDeleted(false)
                .description("Nike, Air Max model")
                .price(new BigDecimal("800.55"))
                .build();

        ResponseEntity<ProductDto> response = controller.createProductForCustomer(customerId, dto);

        verify(mapper, times(1)).toModel(eq(dto));
        verify(mapper, times(1)).toDto(eq(expected));
        verify(manager, times(1)).createProductForCustomer(eq(customerId), any(Product.class));

        assertThat(response).isNotNull();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(expected.getId().toString());
        assertThat(body.getTitle()).isEqualTo(expected.getTitle());
        assertThat(body.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(body.getModifiedAt()).isEqualTo(expected.getModifiedAt());
        assertThat(body.isDeleted()).isEqualTo(expected.isDeleted());
        assertThat(body.getDescription()).isEqualTo(expected.getDescription());
        assertThat(body.getPrice()).isEqualTo(expected.getPrice());
    }

    @Test
    void createProductForCustomer_returnsBadRequest_ifNullBody() {
        ResponseEntity<ProductDto> response = controller.createProductForCustomer("aaa", null);

        verify(mapper, never()).toModel(any(ProductDto.class));
        verify(mapper, never()).toDto(any(Product.class));
        verify(manager, never()).createProductForCustomer(eq("aaa"), any(Product.class));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findAllCustomerProducts_positive() {
        int page = 0;
        int size = 10;
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setTitle("Big Customer");
        List<Product> expectedList = IntStream.range(0, 10)
                .mapToObj(i -> {
                    Product product = new Product();
                    product.setCustomer(customer);
                    product.setCreatedAt(Timestamp.from(Instant.now()));
                    product.setTitle("Product #" + i);
                    return product;
                })
                .collect(Collectors.toList());

        PageImpl<Product> expectedPage = new PageImpl<>(expectedList);

        when(manager.findAllCustomerProductsPageable(eq(customer.getId().toString()), eq(page), eq(size))).thenReturn(expectedPage);

        ResponseEntity<List<ProductDto>> response = controller.findAllCustomerProducts(customer.getId().toString(), page, size);

        verify(mapper, times(1)).toDtos(eq(expectedList));
        verify(manager, times(1)).findAllCustomerProductsPageable(eq(customer.getId().toString()), eq(page), eq(size));
        assertThat(response).isNotNull();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<ProductDto> actualList = response.getBody();
        assertThat(actualList).isNotNull().hasSize(expectedList.size());

        actualList.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getTitle(), o2.getTitle()));
        expectedList.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getTitle(), o2.getTitle()));

        for (int i = 0; i < actualList.size(); i++) {
            ProductDto actual = actualList.get(i);
            Product expected = expectedList.get(i);
            assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
            assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
            assertThat(actual.getModifiedAt()).isEqualTo(expected.getModifiedAt());
            assertThat(actual.isDeleted()).isEqualTo(expected.isDeleted());
            assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
            assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        }
    }

    @Test
    void findAllCustomerProducts_returnsNotFound_whenNoProducts() {
        int page = 0;
        int size = 10;

        when(manager.findAllCustomerProductsPageable(anyString(), eq(page), eq(size))).thenReturn(Page.empty());

        ResponseEntity<List<ProductDto>> response = controller.findAllCustomerProducts("ID", page, size);

        verify(mapper, never()).toDtos(anyList());
        verify(manager, times(1)).findAllCustomerProductsPageable(anyString(), eq(page), eq(size));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findAllCustomerProducts_returnsNotFound_whenPageLargerThanTotalResults() {
        int page = 5;
        int size = 10;
        List<Product> expectedList = IntStream.range(0, 10)
                .mapToObj(i -> {
                    Product product = new Product();
                    product.setCreatedAt(Timestamp.from(Instant.now()));
                    product.setTitle("Product #" + i);
                    return product;
                })
                .collect(Collectors.toList());
        when(manager.findAllCustomerProductsPageable(anyString(), eq(page), eq(size))).thenReturn(new PageImpl<>(expectedList));

        ResponseEntity<List<ProductDto>> response = controller.findAllCustomerProducts("ID", page, size);

        verify(mapper, never()).toDtos(anyList());
        verify(manager, times(1)).findAllCustomerProductsPageable(anyString(), eq(page), eq(size));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateProduct() {
        UUID productId = UUID.randomUUID();
        Product expected = new Product();
        expected.setId(productId);
        expected.setTitle("Air Jordan");
        expected.setCreatedAt(Timestamp.from(Instant.now()));
        expected.setDescription("Nike, Air Jordan model");
        expected.setPrice(new BigDecimal("800.55"));
        expected.setDeleted(false);
        when(manager.updateProduct(eq(productId.toString()), any(UpdateProductDto.class))).thenReturn(expected);

        UpdateProductDto updateDto = UpdateProductDto.builder()
                .title("Air Jordan")
                .description("Nike, Air Jordan model")
                .build();

        ResponseEntity<ProductDto> response = controller.updateProduct(productId.toString(), updateDto);

        verify(mapper, times(1)).toDto(eq(expected));
        verify(manager, times(1)).updateProduct(eq(productId.toString()), eq(updateDto));

        assertThat(response).isNotNull();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(expected.getId().toString());
        assertThat(body.getTitle()).isEqualTo(expected.getTitle());
        assertThat(body.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(body.getModifiedAt()).isEqualTo(expected.getModifiedAt());
        assertThat(body.isDeleted()).isEqualTo(expected.isDeleted());
        assertThat(body.getDescription()).isEqualTo(expected.getDescription());
        assertThat(body.getPrice()).isEqualTo(expected.getPrice());
    }

    @Test
    void findProduct_positive() {
        UUID productId = UUID.randomUUID();
        Product expected = new Product();
        expected.setId(productId);
        expected.setTitle("Air Max");
        expected.setCreatedAt(Timestamp.from(Instant.now()));
        expected.setDescription("Nike, Air Max model");
        expected.setPrice(new BigDecimal("800.55"));
        expected.setDeleted(false);
        when(manager.findProduct(eq(productId.toString()))).thenReturn(Optional.of(expected));

        ResponseEntity<ProductDto> response = controller.findProduct(productId.toString());

        verify(mapper, times(1)).toDto(eq(expected));
        verify(manager, times(1)).findProduct(eq(productId.toString()));

        assertThat(response).isNotNull();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(expected.getId().toString());
        assertThat(body.getTitle()).isEqualTo(expected.getTitle());
        assertThat(body.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(body.getModifiedAt()).isEqualTo(expected.getModifiedAt());
        assertThat(body.isDeleted()).isEqualTo(expected.isDeleted());
        assertThat(body.getDescription()).isEqualTo(expected.getDescription());
        assertThat(body.getPrice()).isEqualTo(expected.getPrice());
    }

    @Test
    void findProduct_returnsNotFound() {
        when(manager.findProduct(anyString())).thenReturn(Optional.empty());

        ResponseEntity<ProductDto> response = controller.findProduct("aaa");

        verify(mapper, never()).toDto(any(Product.class));
        verify(manager, times(1)).findProduct(anyString());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteProduct() {
        controller.deleteProduct("ID");

        verify(manager, times(1)).deleteProduct(eq("ID"));
    }

    @AfterEach
    void tearDown() {
        clearAllCaches();
        manager = null;
        mapper = null;
        controller = null;
    }
}