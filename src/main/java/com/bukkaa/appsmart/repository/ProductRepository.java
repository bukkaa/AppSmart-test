package com.bukkaa.appsmart.repository;

import com.bukkaa.appsmart.entity.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends PagingAndSortingRepository<Product, UUID> {

    List<Product> findAllProductsByCustomerId(UUID customerId);
}
