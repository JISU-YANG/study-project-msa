package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.domain.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    void createOrder(List<Long> productIdList); // 주문 추가 API

    void updateOrder(Long productId); // 주문에 상품 추가

    OrderResponseDto getOrder(Long orderId);
}
