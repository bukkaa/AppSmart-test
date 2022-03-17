package com.bukkaa.appsmart.controller;

import com.bukkaa.appsmart.dto.CustomerDto;
import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import com.bukkaa.appsmart.manager.CustomerManager;
import com.bukkaa.appsmart.mapper.CustomerMapper;
import com.bukkaa.appsmart.mapper.CustomerMapperImpl;
import com.bukkaa.appsmart.mapper.ProductMapperImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class CustomerControllerTest {

    private CustomerManager manager;
    private CustomerMapper mapper;

    private CustomerController controller;


    @BeforeEach
    public void init() {
        manager = mock(CustomerManager.class);
        mapper = spy(CustomerMapperImpl.class);
        ReflectionTestUtils.setField(mapper, "productMapper", spy(ProductMapperImpl.class));
        controller = new CustomerController(manager, mapper);
    }


    @Test
    void createCustomer_positive() {
        Customer expected = new Customer();
        expected.setId(UUID.randomUUID());
        expected.setCreatedAt(Timestamp.from(Instant.now()));
        expected.setTitle("TITLE");
        expected.setDeleted(false);
        when(manager.createCustomer(any(Customer.class))).thenReturn(expected);

        CustomerDto dto = CustomerDto.builder()
                .title("TITLE")
                .isDeleted(false)
                .build();

        ResponseEntity<CustomerDto> response = controller.createCustomer(dto);

        verify(mapper, times(1)).toModel(eq(dto));
        verify(mapper, times(1)).toDto(eq(expected));
        verify(manager, times(1)).createCustomer(any(Customer.class));
        assertThat(response).isNotNull();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(expected.getId().toString());
        assertThat(body.getTitle()).isEqualTo(expected.getTitle());
        assertThat(body.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(body.getModifiedAt()).isEqualTo(expected.getModifiedAt());
        assertThat(body.isDeleted()).isEqualTo(expected.isDeleted());
        assertThat(body.getProducts()).isNotNull().isEmpty();
    }

    @Test
    void createCustomer_returnsBadRequest_ifNullBody() {
        ResponseEntity<CustomerDto> response = controller.createCustomer(null);

        verify(mapper, never()).toModel(any(CustomerDto.class));
        verify(mapper, never()).toDto(any(Customer.class));
        verify(manager, never()).createCustomer(any(Customer.class));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findCustomer_positive() {
        UUID customerId = UUID.randomUUID();
        Customer expected = new Customer();
        expected.setId(customerId);
        expected.setCreatedAt(Timestamp.from(Instant.now()));
        expected.setTitle("TITLE");
        expected.setDeleted(false);
        when(manager.findCustomer(anyString())).thenReturn(Optional.of(expected));
        when(mapper.toDto(eq(expected))).thenCallRealMethod();


        ResponseEntity<CustomerDto> response = controller.findCustomer(customerId.toString());

        verify(mapper, times(1)).toDto(eq(expected));
        verify(manager, times(1)).findCustomer(eq(customerId.toString()));
        assertThat(response).isNotNull();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(expected.getId().toString());
        assertThat(body.getTitle()).isEqualTo(expected.getTitle());
        assertThat(body.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(body.getModifiedAt()).isEqualTo(expected.getModifiedAt());
        assertThat(body.isDeleted()).isEqualTo(expected.isDeleted());
        assertThat(body.getProducts()).isNotNull().isEmpty();
    }

    @Test
    void findCustomer_returnsNotFound() {
        when(manager.findCustomer(anyString())).thenReturn(Optional.empty());

        ResponseEntity<CustomerDto> response = controller.findCustomer("aaa");

        verify(mapper, never()).toDto(any(Customer.class));
        verify(manager, times(1)).findCustomer(anyString());
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllCustomers_positive() {
        int page = 0;
        int size = 10;
        List<Customer> expectedList = IntStream.range(0, size)
                .mapToObj(i -> {
                    Customer customer = new Customer();
                    customer.setCreatedAt(Timestamp.from(Instant.now()));
                    customer.setTitle("Customer #" + i);
                    return customer;
                })
                .collect(Collectors.toList());

        Page<Customer> expectedPage = new PageImpl<>(expectedList);

        when(manager.getAllCustomersPageable(eq(page), eq(size))).thenReturn(expectedPage);

        ResponseEntity<List<CustomerDto>> response = controller.getAllCustomersPageable(page, size);

        verify(mapper, times(1)).toDtos(eq(expectedList));
        verify(manager, times(1)).getAllCustomersPageable(eq(page), eq(size));
        assertThat(response).isNotNull();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<CustomerDto> actualList = response.getBody();
        assertThat(actualList).isNotNull().hasSize(expectedList.size());

        actualList.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getTitle(), o2.getTitle()));
        expectedList.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getTitle(), o2.getTitle()));

        for (int i = 0; i < actualList.size(); i++) {
            CustomerDto actual = actualList.get(i);
            Customer expected = expectedList.get(i);
            assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
            assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
            assertThat(actual.getModifiedAt()).isEqualTo(expected.getModifiedAt());
            assertThat(actual.isDeleted()).isEqualTo(expected.isDeleted());
            assertThat(actual.getProducts()).isNotNull().isEmpty();
        }
    }

    @Test
    void getAllCustomers_returnsNotFound_whenNoCustomers() {
        int page = 0;
        int size = 10;
        when(manager.getAllCustomersPageable(page, size)).thenReturn(Page.empty());

        ResponseEntity<List<CustomerDto>> response = controller.getAllCustomersPageable(page, size);

        verify(mapper, never()).toDtos(anyList());
        verify(manager, times(1)).getAllCustomersPageable(eq(page), eq(size));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllCustomers_returnsNotFound_whenPageLargerThanTotalResults() {
        int page = 5;
        int size = 10;
        List<Customer> expectedList = IntStream.range(0, 3)
                .mapToObj(i -> {
                    Customer customer = new Customer();
                    customer.setCreatedAt(Timestamp.from(Instant.now()));
                    customer.setTitle("Customer #" + i);
                    return customer;
                })
                .collect(Collectors.toList());
        when(manager.getAllCustomersPageable(page, size)).thenReturn(new PageImpl<>(expectedList));

        ResponseEntity<List<CustomerDto>> response = controller.getAllCustomersPageable(page, size);

        verify(mapper, never()).toDtos(anyList());
        verify(manager, times(1)).getAllCustomersPageable(eq(page), eq(size));
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateCustomer() {
        Customer expected = new Customer();
        UUID customerId = UUID.randomUUID();
        expected.setId(customerId);
        expected.setCreatedAt(Timestamp.from(Instant.now()));
        expected.setModifiedAt(Timestamp.from(Instant.now()));
        expected.setTitle("TITLE 2");
        expected.setDeleted(false);
        when(manager.updateCustomer(eq(customerId.toString()), any(UpdateCustomerDto.class))).thenReturn(expected);

        UpdateCustomerDto updateDto = UpdateCustomerDto.builder().title("TITLE 2").build();

        ResponseEntity<CustomerDto> response = controller.updateCustomer(customerId.toString(), updateDto);

        verify(mapper, times(1)).toDto(eq(expected));
        verify(manager, times(1)).updateCustomer(eq(customerId.toString()), eq(updateDto));

        assertThat(response).isNotNull();
        assertThat(response.hasBody()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomerDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(expected.getId().toString());
        assertThat(body.getTitle()).isEqualTo(expected.getTitle());
        assertThat(body.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(body.getModifiedAt()).isEqualTo(expected.getModifiedAt());
        assertThat(body.isDeleted()).isEqualTo(expected.isDeleted());
        assertThat(body.getProducts()).isNotNull().isEmpty();
    }

    @Test
    void removeCustomer() {
        controller.removeCustomer("ID");

        verify(manager, times(1)).removeCustomer(eq("ID"));
    }


    @AfterEach
    public void cleanUp() {
        clearAllCaches();
        manager = null;
        mapper = null;
        controller = null;
    }
}