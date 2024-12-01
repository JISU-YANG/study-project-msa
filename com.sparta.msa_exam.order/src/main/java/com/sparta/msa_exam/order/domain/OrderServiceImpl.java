package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.domain.dto.OrderResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public void createOrder(List<Long> productIdList) {}

    @Override
    public void updateOrder(Long productId) {}

    @Override
    public OrderResponseDto getOrder(Long orderId) { return null; }
}