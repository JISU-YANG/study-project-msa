package com.sparta.msa_exam.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.msa_exam.product.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponseDto {
    @JsonProperty("product_id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("supply_price")
    private Integer supplyPrice;

    public static ProductResponseDto toEntity(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getSupplyPrice()
        );
    }
}
