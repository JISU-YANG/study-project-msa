package com.sparta.msa_exam.product.domain;

import com.sparta.msa_exam.product.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Override
    public List<Product> getAllProducts() { return List.of(); }

    @Override
    public void createProduct(String name, Integer supply_price) {}
}
