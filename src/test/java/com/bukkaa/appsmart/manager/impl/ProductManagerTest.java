package com.bukkaa.appsmart.manager.impl;

import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Product;
import com.bukkaa.appsmart.manager.ProductManager;
import com.bukkaa.appsmart.mapper.ProductMapper;
import com.bukkaa.appsmart.repository.ProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ProductManagerTest {

    private static ProductRepository repository;
    private static ProductManager manager;
    private static ProductMapper mapper;

    @BeforeAll
    static void setUp() {
        repository = mock(ProductRepository.class);
        mapper = spy(ProductMapper.class);
        manager = new ProductManagerImpl(repository, null, mapper);
    }

    @Test
    void findProduct_willThrowIllegalArgumentEx_ifWrongId() {
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
    void deleteProduct_willThrowIllegalArgumentEx_ifWrongId() {
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
    void findAllCustomerProducts_willThrowIllegalArgumentEx_ifWrongId() {
        String inappropriateId = "a";
        Exception expected = null;
        try {
            manager.findAllCustomerProducts(inappropriateId);
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, never()).findById(any());
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo("Invalid UUID string: " + inappropriateId);
    }

    @Test
    void updateProduct_willThrowIllegalArgumentEx_ifWrongId() {
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

    @AfterAll
    static void tearDown() {
        clearAllCaches();
        repository = null;
        manager = null;
    }
}