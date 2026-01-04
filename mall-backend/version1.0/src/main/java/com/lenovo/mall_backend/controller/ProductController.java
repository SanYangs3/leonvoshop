package com.lenovo.mall_backend.controller;

import com.lenovo.mall_backend.entity.Product;
import com.lenovo.mall_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 1. 获取所有商品
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 2. 分页获取商品
    @GetMapping("/page")
    public ResponseEntity<Page<Product>> getProductsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
            Page<Product> productPage = productService.getProductsByPage(pageable);
            return ResponseEntity.ok(productPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 3. 根据ID获取商品
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 4. 搜索商品
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        try {
            List<Product> products = productService.searchProducts(keyword);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 5. 根据品牌搜索
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable String brand) {
        try {
            List<Product> products = productService.getProductsByBrand(brand);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 6. 根据价格范围搜索
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        try {
            List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 7. 查询有库存的商品
    @GetMapping("/in-stock")
    public ResponseEntity<List<Product>> getInStockProducts() {
        try {
            List<Product> products = productService.getInStockProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 8. 根据评分搜索
    @GetMapping("/rating")
    public ResponseEntity<List<Product>> getProductsByRating(@RequestParam Double minRating) {
        try {
            List<Product> products = productService.getProductsByRating(minRating);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 9. 添加商品
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.addProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 10. 更新商品
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            if (e.getMessage().contains("未找到")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    // 11. 更新商品价格 - 需要改为接收BigDecimal
    @PatchMapping("/{id}/price")
    public ResponseEntity<?> updateProductPrice(@PathVariable Integer id, @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal newPrice = request.get("price");
            Product updatedProduct = productService.updateProductPrice(id, newPrice);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            if (e.getMessage().contains("未找到")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    // 12. 增加商品库存
    @PostMapping("/{id}/increase-stock")
    public ResponseEntity<?> increaseStock(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            Product updatedProduct = productService.increaseStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            if (e.getMessage().contains("未找到")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    // 13. 减少商品库存
    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<?> decreaseStock(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            Product updatedProduct = productService.decreaseStock(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            if (e.getMessage().contains("未找到")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        }
    }

    // 14. 删除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "商品删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 15. 获取商品统计信息
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        try {
            Map<String, Object> stats = productService.getProductStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 16. 检查库存
    @GetMapping("/{id}/check-stock")
    public ResponseEntity<?> checkStock(@PathVariable Integer id, @RequestParam Integer quantity) {
        try {
            boolean isAvailable = productService.checkStock(id, quantity);
            Map<String, Object> response = new HashMap<>();
            response.put("productId", id);
            response.put("quantity", quantity);
            response.put("isAvailable", isAvailable);

            if (!isAvailable) {
                int currentStock = productService.getProductStock(id);
                response.put("currentStock", currentStock);
                response.put("message", "库存不足");
                return ResponseEntity.ok(response);
            }

            response.put("message", "库存充足");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 17. 获取低库存商品
    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockProducts(@RequestParam(defaultValue = "10") Integer threshold) {
        try {
            List<Product> lowStockProducts = productService.getLowStockProducts(threshold);
            return ResponseEntity.ok(lowStockProducts);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 18. 获取热门商品
    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedProducts(@RequestParam(required = false) Integer limit) {
        try {
            List<Product> topProducts = productService.getTopRatedProducts(limit);
            return ResponseEntity.ok(topProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 19. 获取最新商品
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestProducts(@RequestParam(required = false) Integer limit) {
        try {
            List<Product> latestProducts = productService.getLatestProducts(limit);
            return ResponseEntity.ok(latestProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 20. 批量添加商品
    @PostMapping("/batch")
    public ResponseEntity<?> addProducts(@RequestBody List<Product> products) {
        try {
            List<Product> addedProducts = productService.addProducts(products);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "成功添加 " + addedProducts.size() + " 个商品");
            response.put("products", addedProducts);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 21. 获取商品总数
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getProductCount() {
        try {
            long count = productService.getProductCount();
            Map<String, Long> response = new HashMap<>();
            response.put("total", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}