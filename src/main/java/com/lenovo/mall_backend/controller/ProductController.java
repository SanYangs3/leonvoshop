package com.lenovo.mall_backend.controller;

import com.lenovo.mall_backend.entity.Product;
import com.lenovo.mall_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. 获取所有商品
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2. 搜索商品（新增的方法）
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        System.out.println("搜索关键词: " + keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            // 如果关键词为空，返回所有商品
            return productRepository.findAll();
        }

        // 调用搜索方法
        return productRepository.findByNameContaining(keyword);
    }

    // 3. 根据ID获取单个商品
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    // 4. 添加商品（测试用）
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
}