package com.bukkaa.appsmart.manager;

import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductManager {

    Optional<Product> findProduct(String productId);

    Product updateProduct(String productId, UpdateProductDto updateProductDto);

    void deleteProduct(String productId);

    List<Product> findAllCustomerProducts(String customerId);

    Product createProductForCustomer(String customerId, Product product);
}
