package com.sparta.msa_exam.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ProductRequestDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("supply_price")
    private Integer supplyPrice;
}
