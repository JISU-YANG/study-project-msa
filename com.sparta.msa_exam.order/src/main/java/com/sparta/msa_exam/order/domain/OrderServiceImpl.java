package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.domain.dto.OrderProductDto;
import com.sparta.msa_exam.order.domain.model.Order;
import com.sparta.msa_exam.order.domain.model.OrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public void createOrder(List<OrderProductDto> productList) {
        Order order = Order.createOrder();
        List<OrderProduct> orderProductList = productList.stream()
                .map(dto -> OrderProduct.createOrderProduct(
                        order, dto.getProductId(), dto.getAmount()
                )).collect(Collectors.toList());

        order.updateOrder(orderProductList);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void updateOrder(Long orderId, List<OrderProductDto> productList) {
        Order order = validateOrder(orderId);
        List<OrderProduct> orderProductList = productList.stream()
                .map(dto -> OrderProduct.createOrderProduct(
                        order, dto.getProductId(), dto.getAmount()
                )).collect(Collectors.toList());

        order.updateOrder(orderProductList);
    }

    @Transactional
    @Override
    public void deleteOrder(Long orderId) {
        Order order = validateOrder(orderId);
        orderRepository.delete(order);
    }

    @Override
    public Order getOrder(Long orderId) {
        return validateOrder(orderId);
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    private Order validateOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }
}