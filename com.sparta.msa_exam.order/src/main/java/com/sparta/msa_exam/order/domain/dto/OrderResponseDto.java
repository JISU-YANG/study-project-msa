package com.sparta.msa_exam.order.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.msa_exam.order.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDto implements Serializable {
    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_ids")
    private List<OrderProductDto> productIdList;

    public static OrderResponseDto toEntity(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getProductList().stream()
                        .map(OrderProductDto::toEntity)
                        .toList()
        );
    }
}
