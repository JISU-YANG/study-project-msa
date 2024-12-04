package com.sparta.msa_exam.product.domain;

import com.sparta.msa_exam.product.domain.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto createProduct(String name, Integer supplyPrice);

    ProductResponseDto getProductById(Long id);

    void deleteProductById(Long id);

    ProductResponseDto updateProduct(Long id, String name, Integer supplyPrice);

    boolean existProductById(Long productId);
}
