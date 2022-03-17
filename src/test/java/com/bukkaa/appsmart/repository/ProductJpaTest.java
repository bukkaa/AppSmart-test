package com.bukkaa.appsmart.repository;

import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.entity.Product;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.manager.ProductManager;
import com.bukkaa.appsmart.manager.impl.ProductManagerImpl;
import com.bukkaa.appsmart.mapper.ProductMapper;
import org.h2.tools.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class ProductJpaTest extends RepositoryBaseTest<ProductRepository, Product, UUID> {

    private ProductManager manager;

    @Autowired
    private CustomerManager customerManager;
    @Autowired
    private ProductMapper mapper;


    @BeforeEach
    void setUp() {
        manager = new ProductManagerImpl(repository, customerManager, spy(mapper));
    }

    @Test
    void findProduct_negative() {
        Optional<Product> resultOpt = repository.findById(UUID.randomUUID());

        assertThat(resultOpt).isNotNull().isEmpty();
    }

    @Test
    void findProduct_positive() {
        Product expected = new Product();
        expected.setTitle("TITLE");
        expected.setCreatedAt(Timestamp.from(Instant.now()));
        expected.setDeleted(false);
        expected.setDescription("DESCRIPTION");
        expected.setPrice(BigDecimal.ONE);

        UUID productId = testEntityManager.persistAndGetId(expected, UUID.class);
        expected.setId(productId);
        assertThat(productId).isNotNull();

        Optional<Product> resultOpt = repository.findById(productId);

        assertThat(resultOpt).isNotNull().isNotEmpty();
        Product actual = resultOpt.get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateProduct() {
        Product initial = new Product();
        initial.setTitle("TITLE");
        initial.setCreatedAt(Timestamp.from(Instant.now()));
        initial.setPrice(BigDecimal.ONE);

        UUID productId = testEntityManager.persistAndGetId(initial, UUID.class);

        assertThat(productId).isNotNull();

        UpdateProductDto updateDto = UpdateProductDto.builder()
                .price(BigDecimal.TEN)
                .isDeleted(true)
                .build();

        Product updated = manager.updateProduct(productId.toString(), updateDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isNotNull().isEqualTo(productId);
        assertThat(updated.getCreatedAt()).isEqualTo(initial.getCreatedAt());
        assertThat(updated.getModifiedAt()).isNotNull();
        assertThat(updated.isDeleted()).isTrue();
        assertThat(updated.getPrice()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void deleteProduct() {
        Product initial = new Product();
        initial.setTitle("TITLE");
        initial.setCreatedAt(Timestamp.from(Instant.now()));
        initial.setPrice(BigDecimal.ONE);

        UUID productId = testEntityManager.persistAndGetId(initial, UUID.class);

        assertThat(productId).isNotNull();

        repository.deleteById(productId);

        Product result = testEntityManager.find(Product.class, productId);
        assertThat(result).isNull();
    }

    @Test
    void findAllCustomerProducts_negative() {
        List<Product> result = manager.findAllCustomerProducts(UUID.randomUUID().toString());

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    void findAllCustomerProducts_positive() {
        Customer customer = new Customer();
        customer.setCreatedAt(Timestamp.from(Instant.now()));
        customer.setTitle("The very big Customer");
        customer.setDeleted(false);

        UUID customerId = testEntityManager.persistAndGetId(customer, UUID.class);
        customer.setId(customerId);

        assertThat(customerId).isNotNull();

        IntStream.range(0, 10)
                .mapToObj(i -> {
                    Product product = new Product();
                    product.setCustomer(customer);
                    product.setTitle("Product #" + i);
                    product.setCreatedAt(Timestamp.from(Instant.now()));
                    product.setPrice(new BigDecimal(i * 1000 + ".00"));
                    return product;
                })
                .forEach(testEntityManager::persist);

        List<Product> result = manager.findAllCustomerProducts(customerId.toString());

        assertThat(result).isNotNull().isNotEmpty().hasSize(10);
    }

    @Test
    void createProductForCustomer() {
        Customer customer = new Customer();
        customer.setTitle("The very big Customer");
        customer.setDeleted(false);

        customerManager.createCustomer(customer);
        assertThat(customer.getId()).isNotNull();

        Product product = new Product();
        product.setTitle("The Product");
        product.setPrice(new BigDecimal("1000.00"));
        product.setDeleted(false);

        Product actual = manager.createProductForCustomer(customer.getId().toString(), product);

        testEntityManager.flush();
        testEntityManager.refresh(customer);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getCustomer()).isNotNull();
        assertThat(actual.getTitle()).isEqualTo(product.getTitle());
        assertThat(actual.getPrice()).isEqualTo(product.getPrice());
        assertThat(actual.isDeleted()).isEqualTo(product.isDeleted());

        Optional<Customer> updatedCustomerOpt = customerManager.findCustomer(customer.getId().toString());
        assertThat(updatedCustomerOpt).isNotNull().isNotEmpty();

        Customer updatedCustomer = updatedCustomerOpt.get();
        assertThat(updatedCustomer.getProducts()).isNotNull().hasSize(1)
                .containsExactly(actual);
    }
}