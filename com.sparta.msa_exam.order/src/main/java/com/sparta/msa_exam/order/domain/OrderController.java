package com.sparta.msa_exam.order.domain;

import com.sparta.msa_exam.order.domain.dto.OrderRequestDto;
import com.sparta.msa_exam.order.domain.dto.OrderResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public String createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        return orderService.createOrder(orderRequestDto.getOrderProductDtoList()) == null ?
                "주문이 성공적으로 되었습니다." : "상품 정보를 가져올 수 없습니다. 다시 시도 해주세요.";
    }

    @GetMapping
    public List<OrderResponseDto> getOrders() {
        return orderService.getOrders().stream().toList();
    }

    @GetMapping("/{order_id}")
    public OrderResponseDto getOrder(@PathVariable("order_id") Long orderId) {
        return orderService.getOrder(orderId);
    }

    @PutMapping("/{order_id}")
    public void updateOrder(@PathVariable("order_id") Long orderId, @Valid @RequestBody OrderRequestDto orderRequestDto) {
        orderService.updateOrder(orderId, orderRequestDto.getOrderProductDtoList());
    }

    @DeleteMapping("/{order_id}")
    public void deleteOrder(@PathVariable("order_id") Long orderId) {
        orderService.deleteOrder(orderId);
    }
}
