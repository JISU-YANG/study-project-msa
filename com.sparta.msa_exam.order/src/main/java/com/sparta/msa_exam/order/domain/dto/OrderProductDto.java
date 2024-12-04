package com.sparta.msa_exam.order.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.msa_exam.order.domain.model.OrderProduct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class OrderProductDto implements Serializable {
    @JsonProperty("product_id")
    @NotNull(message = "상품 ID를 입력해주세요.")
    private Long productId;

    @JsonProperty("amount")
    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    private Integer amount;

    public static OrderProductDto toEntity(OrderProduct orderProduct) {
        return new OrderProductDto(
                orderProduct.getProductId(),
                orderProduct.getAmount()
        );
    }
}
