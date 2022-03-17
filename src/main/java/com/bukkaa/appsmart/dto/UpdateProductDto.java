package com.bukkaa.appsmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductDto implements Serializable {

    private String title;

    private String description;

    private BigDecimal price;

    private boolean isDeleted;
}
