package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.domain.dto.OrderProductDto;
import com.sparta.msa_exam.order.domain.model.Order;
import com.sparta.msa_exam.order.domain.model.OrderProduct;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;


    @Transactional
    @Override
    @CircuitBreaker(name = "OrderService", fallbackMethod = "fallbackCreateOrder")
    public boolean createOrder(List<OrderProductDto> productList) {
        Order order = Order.createOrder();

        if (productList == null || productList.isEmpty()) {
            log.error("Product list is null or empty");
            throw new RuntimeException("Product list is null or empty");
        }

        order.updateOrder(
                validateProductList(order, productList)
        );
        orderRepository.save(order);

        return true;
    }

    public boolean fallbackCreateOrder(List<OrderProductDto> productList, Throwable t) {
        log.error("Create order failed", t);
        return false;
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

    @PostConstruct
    public void registerEventListener() {
        circuitBreakerRegistry.circuitBreaker("validateProductList").getEventPublisher()
                .onStateTransition(event -> log.info("CircuitBreaker State Transition: {}", event)) // 상태 전환 이벤트 리스너
                .onFailureRateExceeded(event -> log.info("CircuitBreaker Failure Rate Exceeded: {}", event)) // 실패율 초과 이벤트 리스너
                .onCallNotPermitted(event -> log.info("CircuitBreaker Call Not Permitted: {}", event)) // 호출 차단 이벤트 리스너
                .onError(event -> log.info("CircuitBreaker Error: {}", event)); // 오류 발생 이벤트 리스너
    }

}