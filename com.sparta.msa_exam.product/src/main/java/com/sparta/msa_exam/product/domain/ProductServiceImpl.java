package com.sparta.msa_exam.product.domain;

import com.sparta.msa_exam.product.domain.dto.ProductResponseDto;
import com.sparta.msa_exam.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Cacheable(cacheNames = "productAllCache", key = "methodName")
    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::toEntity)
                .collect(Collectors.toList());
    }

    @CachePut(cacheNames = "productCache", key = "#result.id")
    @CacheEvict(cacheNames = "productAllCache", allEntries = true)
    @Transactional
    @Override
    public ProductResponseDto createProduct(String name, Integer supplyPrice) {
        Product product = Product.createProduct(name, supplyPrice);
        return ProductResponseDto.toEntity(
                productRepository.save(product)
        );
    }

    @Cacheable(cacheNames = "orderCache", key = "args[0]")
    @Override
    public ProductResponseDto getProductById(Long id) {
        return ProductResponseDto.toEntity(
                validateProduct(id)
        );
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "productAllCache", allEntries = true),
            @CacheEvict(cacheNames = "productCache", key = "args[0]")
    })
    @Transactional
    @Override
    public void deleteProductById(Long id) {
        Product product = validateProduct(id);
        productRepository.delete(product);
    }

    @CachePut(cacheNames = "productCache", key = "args[0]")
    @CacheEvict(cacheNames = "productAllCache", allEntries = true)
    @Transactional
    @Override
    public ProductResponseDto updateProduct(Long id, String name, Integer supplyPrice) {
        Product product = validateProduct(id);
        product.updateProduct(name, supplyPrice);
        return ProductResponseDto.toEntity(
                productRepository.save(product)
        );
    }

    @Override
    public boolean existProductById(Long productId) {
        return productRepository.findById(productId).isPresent();
    }

    protected Product validateProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }
}
