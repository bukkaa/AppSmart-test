package com.bukkaa.appsmart.manager.impl;

import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.mapper.CustomerMapper;
import com.bukkaa.appsmart.repository.CustomerRepository;
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
class CustomerManagerTest {

    private CustomerRepository repository;
    private CustomerManager manager;
    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerRepository.class);
        mapper = spy(CustomerMapper.class);
        manager = new CustomerManagerImpl(repository, mapper);
    }

    @Test
    void findCustomer_throwsIllegalArgumentEx_ifWrongId() {
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
    void removeCustomer_throwsIllegalArgumentEx_ifWrongId() {
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
    void updateCustomer_throwsIllegalArgumentEx_ifWrongId() {
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

    @Test
    void updateCustomer_throwsIllegalArgumentEx_ifNoCustomerFound() {
        UUID customerId = UUID.randomUUID();

        when(repository.findById(eq(customerId))).thenReturn(Optional.empty());

        Exception expected = null;
        try {
            manager.updateCustomer(customerId.toString(), new UpdateCustomerDto());
        } catch (Exception ex) {
            expected = ex;
        }

        verify(repository, times(1)).findById(eq(customerId));
        verify(mapper, never()).merge(any(Customer.class), any(UpdateCustomerDto.class));
        verify(repository, never()).save(any(Customer.class));
        assertThat(expected).isNotNull().isInstanceOf(IllegalArgumentException.class);
        assertThat(expected.getMessage()).isEqualTo(format("Customer with id = '%s' not found!", customerId));
    }

    @AfterEach
    void tearDown() {
        clearAllCaches();
        repository = null;
        manager = null;
        mapper = null;
    }
}