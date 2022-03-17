package com.bukkaa.appsmart.manager.impl;

import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.mapper.CustomerMapper;
import com.bukkaa.appsmart.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerManagerImpl implements CustomerManager {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    public Optional<Customer> findCustomer(String customerId) {
        return repository.findById(UUID.fromString(customerId));
    }

    @Override
    public Customer createCustomer(Customer customer) {
        customer.setCreatedAt(Timestamp.from(Instant.now()));
        return repository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return (List<Customer>) repository.findAll();
    }

    @Override
    public void removeCustomer(String customerId) {
        repository.deleteById(UUID.fromString(customerId));
    }

    @Override
    public Customer updateCustomer(String customerId, UpdateCustomerDto updateCustomerDto) {
        return repository.findById(UUID.fromString(customerId))
                .map(customerToUpdate -> mapper.merge(customerToUpdate, updateCustomerDto))
                .map(repository::save)
                .orElseThrow(() -> {
                    String message = format("Customer with id = '%s' not found!", customerId);
                    log.info("updateCustomer >>> {}", message);
                    throw new IllegalArgumentException(message);
                });
    }
}
