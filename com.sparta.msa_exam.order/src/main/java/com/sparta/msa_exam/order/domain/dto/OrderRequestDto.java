package com.sparta.msa_exam.order.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDto {
    @JsonProperty("order_product")
    private List<OrderProductDto> orderProductDtoList;
}
