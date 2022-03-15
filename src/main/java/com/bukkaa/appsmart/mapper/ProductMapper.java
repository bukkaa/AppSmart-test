package com.bukkaa.appsmart.mapper;

import com.bukkaa.appsmart.dto.ProductDto;
import com.bukkaa.appsmart.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductMapper {

    @Mapping(target = "id", expression = "java(model.getId().toString())")
    ProductDto toDto(Product model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Product toModel(ProductDto dto);
}
