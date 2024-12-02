package com.sparta.msa_exam.product.domain;

import com.sparta.msa_exam.product.domain.dto.ProductRequestDto;
import com.sparta.msa_exam.product.domain.dto.ProductResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public void addProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        productService.createProduct(requestDto.getName(), requestDto.getSupplyPrice());
    }

    @GetMapping
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .map(ProductResponseDto::toEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{product_id}")
    public ProductResponseDto getProductById(@PathVariable(name = "product_id") Long productId) {
        return ProductResponseDto.toEntity(
                productService.getProductById(productId)
        );
    }

    @PutMapping("/{product_id}")
    public void updateProduct(@PathVariable(name = "product_id") Long id, @Valid @RequestBody ProductRequestDto requestDto) {
        productService.updateProduct(id, requestDto.getName(), requestDto.getSupplyPrice());
    }

    @DeleteMapping("/{product_id}")
    public void deleteProduct(@PathVariable(name = "product_id") Long productId) {
        productService.deleteProductById(productId);
    }

}
