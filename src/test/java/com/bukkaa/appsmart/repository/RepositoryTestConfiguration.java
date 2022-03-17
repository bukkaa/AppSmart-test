package com.bukkaa.appsmart.repository;

import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.manager.impl.CustomerManagerImpl;
import com.bukkaa.appsmart.mapper.CustomerMapper;
import com.bukkaa.appsmart.mapper.CustomerMapperImpl;
import com.bukkaa.appsmart.mapper.ProductMapper;
import com.bukkaa.appsmart.mapper.ProductMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EntityScan("com.bukkaa.appsmart.entity")
@EnableJpaRepositories("com.bukkaa.appsmart.repository")
public class RepositoryTestConfiguration {

    @Bean
    public CustomerManager customerManager(@Autowired CustomerRepository customerRepository,
                                           @Autowired CustomerMapper customerMapper) {
        return new CustomerManagerImpl(customerRepository, customerMapper);
    }

    @Bean
    public CustomerMapper customerMapper() {
        return new CustomerMapperImpl();
    }

    @Bean
    public ProductMapper productMapper() {
        return new ProductMapperImpl();
    }
}
