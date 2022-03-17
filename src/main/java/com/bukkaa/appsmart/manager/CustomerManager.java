package com.bukkaa.appsmart.manager;

import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CustomerManager {

    Optional<Customer> findCustomer(String customerId);

    Customer createCustomer(Customer customer);

    Page<Customer> getAllCustomersPageable(int page, int size);

    void removeCustomer(String customerId);

    Customer updateCustomer(String customerId, UpdateCustomerDto updateCustomerDto);
}
