package com.bukkaa.appsmart.manager.impl;

import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Product;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.manager.ProductManager;
import com.bukkaa.appsmart.mapper.ProductMapper;
import com.bukkaa.appsmart.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ProductManagerTest {

    private ProductRepository repository;
    private ProductManager manager;
    private ProductMapper mapper;

    private CustomerManager customerManager;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        mapper = spy(ProductMapper.class);
        customerManager = mock(CustomerManager.class);
        manager = new ProductManagerImpl(repository, customerManager, mapper);
    }

    @Test
    void findProduct_throwsIllegalArgumentEx_ifWrongId() {
        String inappropriateId = "a";
        Exception expected = null;
        try {
            manager.findProduct(inappropriateId);
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, never()).findById(any());
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo("Invalid UUID string: " + inappropriateId);
    }

    @Test
    void deleteProduct_throwsIllegalArgumentEx_ifWrongId() {
        String inappropriateId = "a";
        Exception expected = null;
        try {
            manager.deleteProduct(inappropriateId);
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, never()).findById(any());
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo("Invalid UUID string: " + inappropriateId);
    }

    @Test
    void findAllCustomerProducts_throwsIllegalArgumentEx_ifWrongId() {
        int page = 0;
        int size = 10;
        String inappropriateId = "a";
        Exception expected = null;
        try {
            manager.findAllCustomerProductsPageable(inappropriateId, page, size);
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, never()).findById(any());
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo("Invalid UUID string: " + inappropriateId);
    }

    @Test
    void updateProduct_throwsIllegalArgumentEx_ifWrongId() {
        String inappropriateId = "a";
        Exception expected = null;
        try {
            manager.updateProduct(inappropriateId, new UpdateProductDto());
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, never()).findById(any());
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo("Invalid UUID string: " + inappropriateId);
    }

    @Test
    void updateProduct_throwsIllegalArgumentEx_ifNoProductFound() {
        UUID productId = UUID.randomUUID();

        when(repository.findById(eq(productId))).thenReturn(Optional.empty());

        Exception expected = null;
        try {
            manager.updateProduct(productId.toString(), new UpdateProductDto());
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, times(1)).findById(eq(productId));
        verify(mapper, never()).merge(any(Product.class), any(UpdateProductDto.class));
        verify(repository, never()).save(any(Product.class));
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo(format("Product with id = '%s' not found!", productId));
    }

    @Test
    void createProductForCustomer_throwsIllegalArgumentEx_ifNoCustomerFound() {
        String customerId = "some id";

        when(customerManager.findCustomer(eq(customerId))).thenReturn(Optional.empty());

        Exception expected = null;
        try {
            manager.createProductForCustomer(customerId, new Product());
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, never()).save(any());
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo("No Customer found with id = '" + customerId + "'");
    }

    @AfterEach
    void tearDown() {
        clearAllCaches();
        repository = null;
        manager = null;
        customerManager = null;
    }
}