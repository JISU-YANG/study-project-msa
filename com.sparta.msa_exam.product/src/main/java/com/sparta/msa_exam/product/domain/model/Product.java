package com.sparta.msa_exam.product.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "supply_price")
    private Integer supplyPrice;

    public static Product createProduct(String name, Integer supplyPrice) {
        return Product.builder()
                .name(name)
                .supplyPrice(supplyPrice)
                .build();
    }

    public void updateProduct(String name, Integer supplyPrice) {
        this.name = name;
        this.supplyPrice = supplyPrice;
    }

}