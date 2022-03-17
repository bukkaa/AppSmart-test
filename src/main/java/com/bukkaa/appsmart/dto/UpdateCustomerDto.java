package com.bukkaa.appsmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerDto implements Serializable {

    private String title;
    private boolean isDeleted;
}
