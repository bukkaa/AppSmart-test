package com.bukkaa.appsmart.controller;

import com.bukkaa.appsmart.dto.CustomerDto;
import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.mapper.CustomerMapper;
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
@RequestMapping("/api/v1/customers/")
public class CustomerController {

    private final CustomerManager manager;
    private final CustomerMapper mapper;


    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody(required = false) CustomerDto dto) {
        log.info("createCustomer <<< dto = {}", dto);
        if (dto == null) {
            log.warn("createCustomer >>> Error: no data received");
            return ResponseEntity.badRequest().build();
        }
        Customer customer = manager.createCustomer(mapper.toModel(dto));
        log.info("createCustomer >>> customer = {}", customer);
        return ResponseEntity.ok(mapper.toDto(customer));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> findCustomer(@PathVariable String customerId) {
        log.info("findCustomer <<< customerId = '{}'", customerId);

        Optional<Customer> customerOpt = manager.findCustomer(customerId);

        log.info("findCustomer >>> customer = {}", customerOpt.isPresent() ? customerOpt.get() : "not found");
        return ResponseEntity.of(customerOpt.map(mapper::toDto));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomersPageable(@RequestParam int page,
                                                                     @RequestParam int size) {
        log.info("getAllCustomers <<< ");

        Page<Customer> customersPage = manager.getAllCustomersPageable(page, size);
        if (customersPage.isEmpty() || page > customersPage.getTotalPages()) {
            log.info("getAllCustomers >>> no customers found!");
            return ResponseEntity.notFound().build();
        }

        log.info("getAllCustomers >>> customers = {}", customersPage);
        return ResponseEntity.ok(mapper.toDtos(customersPage.getContent()));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable String customerId,
                                                      @RequestBody(required = false) UpdateCustomerDto updateDto) {
        log.info("updateCustomer <<< customerId = '{}', update = {}", customerId, updateDto);
        if (updateDto == null) {
            log.warn("updateCustomer >>> Error: no data received");
            return ResponseEntity.badRequest().build();
        }
        Customer customer = manager.updateCustomer(customerId, updateDto);

        log.info("updateCustomer >>> updated = {}", customer);
        return ResponseEntity.ok(mapper.toDto(customer));
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeCustomer(@PathVariable String customerId) {
        log.info("removeCustomer <<< customerId = '{}'", customerId);
        manager.removeCustomer(customerId);
        log.info("removeCustomer >>> success");
    }
}
