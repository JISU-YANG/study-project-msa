package com.sparta.msa_exam.order.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OrderProductDto {
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("amount")
    private Integer amount;
}
