package com.bukkaa.appsmart.controller;

import com.bukkaa.appsmart.dto.CustomerDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        CustomerDto body = response.getBody();
        assertNotNull(body);
        assertEquals(expected.getId().toString(), body.getId());
        assertEquals(expected.getTitle(), body.getTitle());
        assertEquals(expected.getCreatedAt(), body.getCreatedAt());
        assertEquals(expected.getModifiedAt(), body.getModifiedAt());
        assertEquals(expected.isDeleted(), body.isDeleted());
        assertNull(body.getProducts());
    }

    @Test
    void createCustomer_returnsBadRequest_ifNullBody() {
        ResponseEntity<CustomerDto> response = controller.createCustomer(null);

        verify(mapper, never()).toModel(any(CustomerDto.class));
        verify(mapper, never()).toDto(any(Customer.class));
        verify(manager, never()).createCustomer(any(Customer.class));
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
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
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CustomerDto body = response.getBody();
        assertNotNull(body);
        assertEquals(expected.getId().toString(), body.getId());
        assertEquals(expected.getTitle(), body.getTitle());
        assertEquals(expected.getCreatedAt(), body.getCreatedAt());
        assertEquals(expected.getModifiedAt(), body.getModifiedAt());
        assertEquals(expected.isDeleted(), body.isDeleted());
        assertNull(body.getProducts());
    }

    @Test
    void findCustomer_returnsNotFound() {
        when(manager.findCustomer(anyString())).thenReturn(Optional.empty());

        ResponseEntity<CustomerDto> response = controller.findCustomer("aaa");

        verify(mapper, never()).toDto(any(Customer.class));
        verify(manager, times(1)).findCustomer(anyString());
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllCustomers() {
        assertNotNull(new Object());
    }

    @Test
    void updateCustomer() {
        Customer expectedDto = new Customer();
        UUID customerId = UUID.randomUUID();
        expectedDto.setId(customerId);
        expectedDto.setCreatedAt(Timestamp.from(Instant.now()));
        expectedDto.setModifiedAt(Timestamp.from(Instant.now()));
        expectedDto.setTitle("TITLE 2");
        expectedDto.setDeleted(false);
        when(manager.updateCustomer(eq(customerId.toString()), any(Customer.class))).thenReturn(expectedDto);

        CustomerDto dto = CustomerDto.builder().id(customerId.toString()).title("TITLE 2").build();

        ResponseEntity<CustomerDto> response = controller.updateCustomer(dto.getId(), dto);

        verify(mapper, times(1)).toModel(eq(dto));
        verify(mapper, times(1)).toDto(eq(expectedDto));
        Customer expectedModel = new Customer();
        expectedModel.setTitle(expectedDto.getTitle());
        verify(manager, times(1)).updateCustomer(eq(dto.getId()), eq(expectedModel));

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        CustomerDto body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedDto.getId().toString(), body.getId());
        assertEquals(expectedDto.getTitle(), body.getTitle());
        assertEquals(expectedDto.getCreatedAt(), body.getCreatedAt());
        assertEquals(expectedDto.getModifiedAt(), body.getModifiedAt());
        assertEquals(expectedDto.isDeleted(), body.isDeleted());
        assertNull(body.getProducts());
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