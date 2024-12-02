package com.sparta.msa_exam.order.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> productList = new ArrayList<>();

    public static Order createOrder() {
        return Order.builder().build();
    }

    public void updateOrder(List<OrderProduct> orderProductList) {
        this.productList.clear();
        this.productList.addAll(orderProductList);
    }
}
