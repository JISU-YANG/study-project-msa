package com.sparta.msa_exam.order.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.msa_exam.order.domain.model.Order;
import com.sparta.msa_exam.order.domain.model.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponseDto {
    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_ids")
    private List<Long> productIdList;

    public static OrderResponseDto toEntity(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getProductList().stream()
                        .map(OrderProduct::getProductId)
                        .toList()
        );
    }
}
