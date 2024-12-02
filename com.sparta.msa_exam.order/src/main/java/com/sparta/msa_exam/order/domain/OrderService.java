package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.domain.dto.OrderProductDto;
import com.sparta.msa_exam.order.domain.model.Order;

import java.util.List;

public interface OrderService {
    void createOrder(List<OrderProductDto> productList); // 주문 추가 API

    void updateOrder(Long orderId, List<OrderProductDto> productList); // 주문에 상품 추가

    void deleteOrder(Long orderId);

    Order getOrder(Long orderId);

    List<Order> getOrders();
}
