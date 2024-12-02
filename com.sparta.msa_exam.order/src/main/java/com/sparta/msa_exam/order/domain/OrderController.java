package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.domain.dto.OrderRequestDto;
import com.sparta.msa_exam.order.domain.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public void createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        orderService.createOrder(orderRequestDto.getOrderProductDtoList());
    }

    @GetMapping
    public List<OrderResponseDto> getOrders() {
        return orderService.getOrders().stream()
                .map(OrderResponseDto::toEntity)
                .toList();
    }

    @GetMapping("/{order_id}")
    public OrderResponseDto getOrder(@PathVariable("order_id") Long orderId) {
        return OrderResponseDto.toEntity(orderService.getOrder(orderId));
    }

    @PutMapping("/{order_id}")
    public void updateOrder(@PathVariable("order_id") Long orderId, @RequestBody OrderRequestDto orderRequestDto) {
        orderService.updateOrder(orderId, orderRequestDto.getOrderProductDtoList());
    }

    @DeleteMapping("/{order_id}")
    public void deleteOrder(@PathVariable("order_id") Long orderId) {
        orderService.deleteOrder(orderId);
    }
}
