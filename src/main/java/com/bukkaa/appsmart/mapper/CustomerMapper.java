package com.bukkaa.appsmart.mapper;

import com.bukkaa.appsmart.dto.CustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = ProductMapper.class)
public interface CustomerMapper {

    @Mapping(target = "id", expression = "java(model.getId().toString())")
    CustomerDto toDto(Customer model);

    default List<CustomerDto> toDtos(List<Customer> list) {
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "products", ignore = true)
    Customer toModel(CustomerDto dto);

}
