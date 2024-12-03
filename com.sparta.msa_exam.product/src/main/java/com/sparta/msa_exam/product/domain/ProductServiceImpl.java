package com.sparta.msa_exam.product.domain;

import com.sparta.msa_exam.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    @Override
    public void createProduct(String name, Integer supplyPrice) {
        Product product = Product.createProduct(name, supplyPrice);
        productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return validateProduct(id);
    }

    @Transactional
    @Override
    public void deleteProductById(Long id) {
        Product product = validateProduct(id);
        productRepository.delete(product);
    }

    @Transactional
    @Override
    public void updateProduct(Long id, String name, Integer supplyPrice) {
        Product product = validateProduct(id);
        product.updateProduct(name, supplyPrice);
        productRepository.save(product);
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
