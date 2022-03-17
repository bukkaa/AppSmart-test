package com.bukkaa.appsmart.mapper;

import com.bukkaa.appsmart.dto.CustomerDto;
import com.bukkaa.appsmart.dto.UpdateCustomerDto;
import com.bukkaa.appsmart.entity.Customer;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Stream.ofNullable;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(uses = {ItemIdMapper.class, ProductMapper.class})
public interface CustomerMapper {

    @Mapping(target = "id", nullValueCheckStrategy = ALWAYS, qualifiedByName = "uuidToString")
    CustomerDto toDto(Customer model);

    default List<CustomerDto> toDtos(List<Customer> list) {
        return ofNullable(list)
                .flatMap(Collection::stream)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "products", ignore = true)
    Customer toModel(CustomerDto dto);

    @Mapping(target = "modifiedAt", expression = "java( java.sql.Timestamp.from( java.time.Instant.now() ) )")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer merge(@MappingTarget Customer target, UpdateCustomerDto updateDto);
}
