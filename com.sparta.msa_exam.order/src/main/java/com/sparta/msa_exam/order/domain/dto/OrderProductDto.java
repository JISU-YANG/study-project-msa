package com.sparta.msa_exam.order.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderProductDto {
    @JsonProperty("product_id")
    @NotNull(message = "상품 ID를 입력해주세요.")
    private Long productId;

    @JsonProperty("amount")
    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    private Integer amount;
}
