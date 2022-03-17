package com.bukkaa.appsmart.manager;

import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Product;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ProductManager {

    Optional<Product> findProduct(String productId);

    Product updateProduct(String productId, UpdateProductDto updateProductDto);

    void deleteProduct(String productId);

    Page<Product> findAllCustomerProductsPageable(String customerId, int page, int size);

    Product createProductForCustomer(String customerId, Product product);
}
