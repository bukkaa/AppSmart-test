package com.bukkaa.appsmart.repository;

import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.manager.impl.CustomerManagerImpl;
import com.bukkaa.appsmart.mapper.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class CustomerJpaTest extends RepositoryBaseTest<CustomerRepository, Customer, UUID> {

    private CustomerManager manager;

    @Autowired
    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        manager = new CustomerManagerImpl(repository, spy(mapper));
    }

    @Test
    void findCustomer_positive() {
        Customer expected = new Customer();
        expected.setCreatedAt(Timestamp.from(Instant.now()));
        expected.setTitle("TITLE");
        expected.setDeleted(false);

        expected = testEntityManager.persist(expected);

        assertThat(expected.getId()).isNotNull();

        Optional<Customer> resultOpt = repository.findById(expected.getId());

        assertThat(resultOpt).isNotNull().isNotEmpty();
        Customer actual = resultOpt.get();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findCustomer_negative() {
        Optional<Customer> resultOpt = repository.findById(UUID.randomUUID());

        assertThat(resultOpt).isNotNull().isEmpty();
    }

    @Test
    void removeCustomer() {
        Customer entity = new Customer();
        entity.setCreatedAt(Timestamp.from(Instant.now()));
        entity.setTitle("TITLE");
        entity.setDeleted(false);

        UUID customerId = testEntityManager.persistAndGetId(entity, UUID.class);

        assertThat(customerId).isNotNull();

        repository.deleteById(customerId);

        Customer result = testEntityManager.find(Customer.class, customerId);
        assertThat(result).isNull();
    }

    @Test
    void getAllCustomers_negative() {
        int page = 0;
        int size = 10;
        Page<Customer> result = manager.getAllCustomersPageable(page, size);

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    void getAllCustomers_positive() {
        int page = 0;
        int size = 10;
        IntStream.range(0, size)
                .mapToObj(i -> {
                    Customer customer = new Customer();
                    customer.setCreatedAt(Timestamp.from(Instant.now()));
                    customer.setTitle("Customer #" + i);
                    return customer;
                })
                .forEach(testEntityManager::persist);

        Page<Customer> result = manager.getAllCustomersPageable(page, size);

        assertThat(result).isNotNull().isNotEmpty().hasSize(10);
    }

    @Test
    void createCustomer() {
        Customer entity = new Customer();
        entity.setTitle("The very big Customer");
        entity.setDeleted(false);

        Customer persisted = manager.createCustomer(entity);

        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getCreatedAt()).isNotNull();

        Customer actual = testEntityManager.find(Customer.class, persisted.getId());

        assertThat(actual).isEqualTo(persisted);
    }

    @Test
    void updateCustomer() {
        Customer initial = new Customer();
        initial.setCreatedAt(Timestamp.from(Instant.now()));
        initial.setTitle("The very big Customer");
        initial.setDeleted(false);

        UUID customerId = testEntityManager.persistAndGetId(initial, UUID.class);

        assertThat(customerId).isNotNull();

        UpdateCustomerDto updateDto = UpdateCustomerDto.builder()
                .title("Now it's small Customer")
                .isDeleted(true)
                .build();

        Customer updated = manager.updateCustomer(customerId.toString(), updateDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isNotNull().isEqualTo(customerId);
        assertThat(updated.getCreatedAt()).isEqualTo(initial.getCreatedAt());
        assertThat(updated.getModifiedAt()).isNotNull();
        assertThat(updated.getTitle()).isEqualTo("Now it's small Customer");
        assertThat(updated.isDeleted()).isTrue();
    }

}