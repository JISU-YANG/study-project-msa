package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.domain.dto.OrderProductDto;
import com.sparta.msa_exam.order.domain.dto.OrderResponseDto;
import com.sparta.msa_exam.order.domain.model.Order;
import com.sparta.msa_exam.order.domain.model.OrderProduct;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @CachePut(cacheNames = "orderCache", key = "#result.orderId")
    @Transactional
    @Override
    @CircuitBreaker(name = "OrderService", fallbackMethod = "fallbackCreateOrder")
    public OrderResponseDto createOrder(List<OrderProductDto> productList) {
        Order order = Order.createOrder();

        if (productList == null || productList.isEmpty()) {
            log.error("Product list is null or empty");
            throw new RuntimeException("Product list is null or empty");
        }

        order.updateOrder(
                validateProductList(order, productList)
        );

        return OrderResponseDto.toEntity(
                orderRepository.save(order)
        );
    }

    public Order fallbackCreateOrder(List<OrderProductDto> productList, Throwable t) {
        log.error("Create order failed", t);
        return null;
    }

    @CachePut(cacheNames = "orderCache", key = "args[0]")
    @CacheEvict(cacheNames = "orderAllCache", allEntries = true)
    @Transactional
    @Override
    public OrderResponseDto updateOrder(Long orderId, List<OrderProductDto> productList) {
        Order order = validateOrder(orderId);
        order.updateOrder(
                validateProductList(order, productList)
        );
        return OrderResponseDto.toEntity(
                orderRepository.save(order)
        );
    }

    @Cacheable(cacheNames = "orderCache", key = "args[0]")
    @Override
    public OrderResponseDto getOrder(Long orderId) {
        return OrderResponseDto.toEntity(
                validateOrder(orderId)
        );
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "orderAllCache", allEntries = true),
            @CacheEvict(cacheNames = "orderCache", key = "args[0]")
    })
    @Transactional
    @Override
    public void deleteOrder(Long orderId) {
        Order order = validateOrder(orderId);
        orderRepository.delete(order);
    }

    @Cacheable(cacheNames = "orderAllCache", key = "methodName")
    @Override
    public List<OrderResponseDto> getOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDto::toEntity)
                .collect(Collectors.toList());
    }

    private Order validateOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private List<OrderProduct> validateProductList(Order order, List<OrderProductDto> productList) {
        List<OrderProduct> orderProductList = new ArrayList<>();

        for (OrderProductDto dto : productList) {
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