package com.bukkaa.appsmart.controller;

import com.bukkaa.appsmart.dto.ProductDto;
import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Product;
import com.bukkaa.appsmart.manager.ProductManager;
import com.bukkaa.appsmart.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductManager manager;
    private final ProductMapper mapper;


    @PostMapping("/customers/{customerId}/products")
    public ResponseEntity<ProductDto> createProductForCustomer(@PathVariable String customerId,
                                                               @RequestBody(required = false) ProductDto dto) {
        log.info("createProductForCustomer <<< customerId = '{}', dto = {}", customerId, dto);
        if (dto == null) {
            log.warn("createProductForCustomer >>> Error: no data received");
            return ResponseEntity.badRequest().build();
        }
        Product product = manager.createProductForCustomer(customerId, mapper.toModel(dto));
        log.info("createProductForCustomer >>> product = {}", product);
        return ResponseEntity.ok(mapper.toDto(product));
    }

    @GetMapping("/customers/{customerId}/products")
    public ResponseEntity<List<ProductDto>> findAllCustomerProducts(@PathVariable String customerId,
                                                                    @RequestParam int page,
                                                                    @RequestParam int size) {
        log.info("findAllCustomerProducts <<< customerId = '{}'", customerId);

        Page<Product> productsPage = manager.findAllCustomerProductsPageable(customerId, page, size);
        if (productsPage.isEmpty() || page > productsPage.getTotalPages()) {
            log.info("findAllCustomerProducts >>> no products found for customer '{}'!", customerId);
            return ResponseEntity.notFound().build();
        }

        log.info("findAllCustomerProducts >>> customerId = '{}', products = {}", customerId, productsPage);
        return ResponseEntity.ok(mapper.toDtos(productsPage.getContent()));
    }



    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String productId,
                                                    @RequestBody(required = false) UpdateProductDto updateDto) {
        log.info("updateProduct <<< productId = '{}', update = {}", productId, updateDto);
        if (updateDto == null) {
            log.warn("updateProduct >>> Error: no data received");
            return ResponseEntity.badRequest().build();
        }
        Product product = manager.updateProduct(productId, updateDto);

        log.info("updateProduct >>> updated = {}", product);
        return ResponseEntity.ok(mapper.toDto(product));
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductDto> findProduct(@PathVariable String productId) {
        log.info("findProduct <<< productId = '{}'", productId);

        Optional<Product> productOpt = manager.findProduct(productId);

        log.info("findProduct >>> product = {}", productOpt.isPresent() ? productOpt.get() : "not found");
        return ResponseEntity.of(productOpt.map(mapper::toDto));
    }

    @DeleteMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable String productId) {
        log.info("deleteProduct <<< productId = '{}'", productId);
        manager.deleteProduct(productId);
        log.info("deleteProduct >>> success");
    }
}
