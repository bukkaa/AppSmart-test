package com.bukkaa.appsmart.manager.impl;

import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.entity.Product;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.manager.ProductManager;
import com.bukkaa.appsmart.mapper.ProductMapper;
import com.bukkaa.appsmart.repository.ProductRepository;
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
public class ProductManagerImpl implements ProductManager {

    private final ProductRepository repository;
    private final CustomerManager customerManager;
    private final ProductMapper mapper;


    @Override
    public Optional<Product> findProduct(String productId) {
        return repository.findById(UUID.fromString(productId));
    }

    @Override
    public Product updateProduct(String productId, UpdateProductDto updateProductDto) {
        return repository.findById(UUID.fromString(productId))
                .map(productToUpdate -> mapper.merge(productToUpdate, updateProductDto))
                .map(repository::save)
                .orElseThrow(() -> {
                    String message = format("Product with id = '%s' not found!", productId);
                    log.info("updateProduct >>> {}", message);
                    throw new IllegalArgumentException(message);
                });
    }

    @Override
    public void deleteProduct(String productId) {
        repository.deleteById(UUID.fromString(productId));
    }

    @Override
    public List<Product> findAllCustomerProducts(String customerId) {
        return repository.findAllProductsByCustomerId(UUID.fromString(customerId));
    }

    @Override
    public Product createProductForCustomer(String customerId, Product product) {
        Optional<Customer> customer = customerManager.findCustomer(customerId);
        if (customer.isEmpty()) {
            String message = "No Customer found with id = '" + customerId + "'";
            log.info("createProductForCustomer >>> " + message);
            throw new IllegalArgumentException(message);
        }
        product.setCustomer(customer.get());
        product.setCreatedAt(Timestamp.from(Instant.now()));
        log.info("createProductForCustomer :: persisting new product {}", product);
        return repository.save(product);
    }
}
