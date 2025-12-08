package com.lenovo.mall_backend.controller;

import com.lenovo.mall_backend.entity.Product;
import com.lenovo.mall_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. 获取所有商品（不分页，简化版）
    @GetMapping
    public Map<String, Object> getAllProducts() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Product> products = productRepository.findAll();
            result.put("success", true);
            result.put("data", products);
            result.put("count", products.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取商品失败：" + e.getMessage());
        }
        return result;
    }

    // 2. 获取单个商品详情
    @GetMapping("/{id}")
    public Map<String, Object> getProductById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product != null) {
                result.put("success", true);
                result.put("data", product);
            } else {
                result.put("success", false);
                result.put("message", "商品不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取商品失败：" + e.getMessage());
        }
        return result;
    }

    // 3. 添加商品
    @PostMapping
    public Map<String, Object> addProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 设置时间
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());

            // 设置默认值
            if (product.getBrand() == null) product.setBrand("联想");
            if (product.getStock() == null) product.setStock(100);
            if (product.getRating() == null) product.setRating(4.5);

            Product savedProduct = productRepository.save(product);

            result.put("success", true);
            result.put("message", "商品添加成功");
            result.put("data", savedProduct);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "商品添加失败：" + e.getMessage());
        }
        return result;
    }

    // 4. 更新商品
    @PutMapping("/{id}")
    public Map<String, Object> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            Product existingProduct = productRepository.findById(id).orElse(null);
            if (existingProduct != null) {
                // 更新字段
                existingProduct.setName(product.getName());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setDescription(product.getDescription());
                existingProduct.setImageUrl(product.getImageUrl());
                existingProduct.setBrand(product.getBrand());
                existingProduct.setStock(product.getStock());
                existingProduct.setSpecifications(product.getSpecifications());
                existingProduct.setRating(product.getRating());
                existingProduct.setUpdatedAt(LocalDateTime.now());

                Product updatedProduct = productRepository.save(existingProduct);

                result.put("success", true);
                result.put("message", "商品更新成功");
                result.put("data", updatedProduct);
            } else {
                result.put("success", false);
                result.put("message", "商品不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "商品更新失败：" + e.getMessage());
        }
        return result;
    }

    // 5. 删除商品
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteProduct(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                result.put("success", true);
                result.put("message", "商品删除成功");
            } else {
                result.put("success", false);
                result.put("message", "商品不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "商品删除失败：" + e.getMessage());
        }
        return result;
    }

    // 6. 搜索商品
    @GetMapping("/search")
    public Map<String, Object> searchProducts(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Product> products = productRepository.findByNameContaining(keyword);
            result.put("success", true);
            result.put("data", products);
            result.put("count", products.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "搜索失败：" + e.getMessage());
        }
        return result;
    }
}