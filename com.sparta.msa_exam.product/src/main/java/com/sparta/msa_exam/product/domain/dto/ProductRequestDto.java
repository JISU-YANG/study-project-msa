package com.sparta.msa_exam.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProductRequestDto {
    @JsonProperty("name")
    @NotBlank(message = "상품의 이름을 입력해주세요.")
    private String name;

    @JsonProperty("supply_price")
    @Min(value = 100, message = "상품의 공급 가격은 100원 이상이어야 합니다.")
    private Integer supplyPrice;
}
