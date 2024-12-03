package com.sparta.msa_exam.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product")
public interface ProductClient {
    @GetMapping(value = "/products/{product_id}/exists")
    boolean existProductById(@PathVariable(name = "product_id") Long productId);
}