package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.domain.dto.OrderProductDto;
import com.sparta.msa_exam.order.domain.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(List<OrderProductDto> productList); // 주문 추가 API

    OrderResponseDto updateOrder(Long orderId, List<OrderProductDto> productList); // 주문에 상품 추가

    void deleteOrder(Long orderId);

    OrderResponseDto getOrder(Long orderId);

    List<OrderResponseDto> getOrders();
}
