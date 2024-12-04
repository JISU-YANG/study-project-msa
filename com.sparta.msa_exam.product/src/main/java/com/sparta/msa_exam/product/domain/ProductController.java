package com.sparta.msa_exam.product.domain;

import com.sparta.msa_exam.product.domain.dto.ProductRequestDto;
import com.sparta.msa_exam.product.domain.dto.ProductResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public Long addProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        return productService.createProduct(requestDto.getName(), requestDto.getSupplyPrice()).getId();
    }

    @GetMapping
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{product_id}")
    public ProductResponseDto getProductById(@PathVariable(name = "product_id") Long productId) {
        return productService.getProductById(productId);
    }

    @PutMapping("/{product_id}")
    public ProductResponseDto updateProduct(@PathVariable(name = "product_id") Long id, @Valid @RequestBody ProductRequestDto requestDto) {
        return productService.updateProduct(id, requestDto.getName(), requestDto.getSupplyPrice());
    }

    @DeleteMapping("/{product_id}")
    public void deleteProduct(@PathVariable(name = "product_id") Long productId) {
        productService.deleteProductById(productId);
    }

    @GetMapping("/{product_id}/exists")
    public boolean existProductById(@PathVariable(name = "product_id") Long productId) {
        return productService.existProductById(productId);
    }

}
