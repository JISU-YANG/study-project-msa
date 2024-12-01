package com.sparta.msa_exam.product.domain;

import com.sparta.msa_exam.product.domain.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();

    void createProduct(String name, Integer supply_price);
}
