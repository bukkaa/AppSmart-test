package com.bukkaa.appsmart.repository;

import com.bukkaa.appsmart.entity.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, UUID> {

}
