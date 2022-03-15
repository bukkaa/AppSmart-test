package com.bukkaa.appsmart.manager;

import com.bukkaa.appsmart.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerManager {

    Optional<Customer> findCustomer(String customerId);

    Customer createCustomer(Customer customer);

    List<Customer> getAllCustomers();

    void removeCustomer(String customerId);

    Customer updateCustomer(String customerId, Customer customer);
}
