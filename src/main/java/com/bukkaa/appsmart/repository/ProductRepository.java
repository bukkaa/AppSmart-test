package com.bukkaa.appsmart.repository;

import com.bukkaa.appsmart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface ProductRepository extends PagingAndSortingRepository<Product, UUID> {

    Page<Product> findAllProductsByCustomerId(UUID customerId, Pageable pageable);
}
