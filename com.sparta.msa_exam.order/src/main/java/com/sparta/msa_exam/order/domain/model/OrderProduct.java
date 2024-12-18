package com.sparta.msa_exam.order.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_order_product")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    public static OrderProduct createOrderProduct(Order order, Long product_id, Integer amount) {
        return OrderProduct.builder()
                .order(order)
                .productId(product_id)
                .amount(amount)
                .build();
    }
}
