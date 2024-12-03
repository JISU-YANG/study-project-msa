package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.domain.dto.OrderProductDto;
import com.sparta.msa_exam.order.domain.model.Order;
import com.sparta.msa_exam.order.domain.model.OrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Transactional
    @Override
    public void createOrder(List<OrderProductDto> productList) {
        Order order = Order.createOrder();

        order.updateOrder(
                validateProductList(order, productList)
        );
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void updateOrder(Long orderId, List<OrderProductDto> productList) {
        Order order = validateOrder(orderId);
        order.updateOrder(
                validateProductList(order, productList)
        );
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

    private List<OrderProduct> validateProductList(Order order, List<OrderProductDto> productList) {
        List<OrderProduct> orderProductList = new ArrayList<>();

        for(OrderProductDto dto : productList) {
            if (productClient.existProductById(dto.getProductId())) {
                orderProductList.add(OrderProduct.createOrderProduct(order, dto.getProductId(), dto.getAmount()));
            }
        }

        return orderProductList;
    }
}