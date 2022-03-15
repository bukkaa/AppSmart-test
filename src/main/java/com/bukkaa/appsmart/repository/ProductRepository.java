package com.bukkaa.appsmart.repository;

import com.bukkaa.appsmart.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {

}
