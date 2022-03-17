package com.bukkaa.appsmart.manager.impl;

import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.mapper.CustomerMapper;
import com.bukkaa.appsmart.repository.CustomerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class CustomerManagerTest {

    private static CustomerRepository repository;
    private static CustomerManager manager;
    private static CustomerMapper mapper;

    @BeforeAll
    static void setUp() {
        repository = mock(CustomerRepository.class);
        mapper = spy(CustomerMapper.class);
        manager = new CustomerManagerImpl(repository, mapper);
    }

    @Test
    void findCustomer_willThrowIllegalArgumentEx_ifWrongId() {
        String inappropriateId = "a";
        Exception expected = null;
        try {
            manager.findCustomer(inappropriateId);
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, never()).findById(any());
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo("Invalid UUID string: " + inappropriateId);
    }

    @Test
    void removeCustomer_willThrowIllegalArgumentEx_ifWrongId() {
        String inappropriateId = "a";
        Exception expected = null;
        try {
            manager.removeCustomer(inappropriateId);
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, never()).findById(any());
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo("Invalid UUID string: " + inappropriateId);
    }

    @Test
    void updateCustomer_willThrowIllegalArgumentEx_ifWrongId() {
        String inappropriateId = "a";
        Exception expected = null;
        try {
            manager.updateCustomer(inappropriateId, new UpdateCustomerDto());
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