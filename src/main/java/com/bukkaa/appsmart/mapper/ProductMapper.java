package com.bukkaa.appsmart.mapper;

import com.bukkaa.appsmart.dto.ProductDto;
import com.bukkaa.appsmart.dto.UpdateProductDto;
import com.bukkaa.appsmart.entity.Product;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Stream.ofNullable;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(uses = ItemIdMapper.class)
public interface ProductMapper {

    @Mapping(target = "id", nullValueCheckStrategy = ALWAYS, qualifiedByName = "uuidToString")
    @Mapping(target = "isDeleted", source = "deleted")
    ProductDto toDto(Product model);

    default List<ProductDto> toDtos(List<Product> list) {
        return ofNullable(list)
                .flatMap(Collection::stream)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    Product toModel(ProductDto dto);

    @Mapping(target = "modifiedAt", expression = "java( java.sql.Timestamp.from( java.time.Instant.now() ) )")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product merge(@MappingTarget Product target, UpdateProductDto updateDto);
}
