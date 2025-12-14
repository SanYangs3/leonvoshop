package com.lenovo.mall_backend.service;

import com.lenovo.mall_backend.entity.Product;
import com.lenovo.mall_backend.exception.ResourceNotFoundException;
import com.lenovo.mall_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ==================== 查询相关方法 ====================

    /**
     * 获取所有商品
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 分页获取商品
     */
    public Page<Product> getProductsByPage(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * 根据ID获取商品
     */
    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    /**
     * 搜索商品（多字段搜索）
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.searchProducts(keyword);
    }

    /**
     * 根据品牌搜索商品
     */
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    /**
     * 根据价格范围搜索商品
     */
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("价格范围不能为空");
        }
        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("价格不能为负数");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("最小价格不能大于最大价格");
        }
        return productRepository.findByPriceBetween(BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice));
    }

    /**
     * 查询有库存的商品
     */
    public List<Product> getInStockProducts() {
        return productRepository.findInStockProducts();
    }

    /**
     * 根据评分搜索商品
     */
    public List<Product> getProductsByRating(Double minRating) {
        if (minRating == null || minRating < 0 || minRating > 5) {
            throw new IllegalArgumentException("评分必须在0-5之间");
        }
        return productRepository.findByRatingGreaterThanEqual(minRating);
    }

    /**
     * 根据名称和品牌组合搜索
     */
    public List<Product> searchProductsByNameAndBrand(String name, String brand) {
        return productRepository.findByNameContainingAndBrand(name, brand);
    }

    // ==================== 新增相关方法 ====================

    /**
     * 添加单个商品
     */
    @Transactional
    public Product addProduct(Product product) {
        validateProduct(product);

        // 设置默认值
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getRating() == null) {
            product.setRating(0.0);
        }

        // 设置创建和更新时间
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * 批量添加商品
     */
    @Transactional
    public List<Product> addProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("商品列表不能为空");
        }

        // 验证并设置每个商品的默认值
        for (Product product : products) {
            validateProduct(product);

            if (product.getStock() == null) {
                product.setStock(0);
            }
            if (product.getRating() == null) {
                product.setRating(0.0);
            }

            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
        }

        return productRepository.saveAll(products);
    }

    // ==================== 更新相关方法 ====================

    /**
     * 更新商品信息
     */
    @Transactional
    public Product updateProduct(Integer id, Product productDetails) {
        Product existingProduct = getProductById(id);

        // 更新字段（只更新非空字段）
        if (productDetails.getName() != null && !productDetails.getName().trim().isEmpty()) {
            existingProduct.setName(productDetails.getName());
        }

        if (productDetails.getPrice() != null) {
            if (productDetails.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("商品价格必须大于0");
            }
            existingProduct.setPrice(productDetails.getPrice());
        }

        if (productDetails.getDescription() != null) {
            existingProduct.setDescription(productDetails.getDescription());
        }

        if (productDetails.getImageUrl() != null) {
            existingProduct.setImageUrl(productDetails.getImageUrl());
        }

        if (productDetails.getBrand() != null) {
            existingProduct.setBrand(productDetails.getBrand());
        }

        if (productDetails.getStock() != null) {
            if (productDetails.getStock() < 0) {
                throw new IllegalArgumentException("库存不能为负数");
            }
            existingProduct.setStock(productDetails.getStock());
        }

        if (productDetails.getSpecifications() != null) {
            existingProduct.setSpecifications(productDetails.getSpecifications());
        }

        if (productDetails.getRating() != null) {
            if (productDetails.getRating() < 0 || productDetails.getRating() > 5) {
                throw new IllegalArgumentException("评分必须在0-5之间");
            }
            existingProduct.setRating(productDetails.getRating());
        }

        // 更新修改时间
        existingProduct.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(existingProduct);
    }

    /**
     * 更新商品库存
     */
    @Transactional
    public Product updateProductStock(Integer id, Integer newStock) {
        if (newStock == null) {
            throw new IllegalArgumentException("库存数量不能为空");
        }
        if (newStock < 0) {
            throw new IllegalArgumentException("库存不能为负数");
        }

        Product product = getProductById(id);
        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * 增加商品库存
     */
    @Transactional
    public Product increaseStock(Integer id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("增加数量必须大于0");
        }

        Product product = getProductById(id);
        product.setStock(product.getStock() + quantity);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * 减少商品库存
     */
    @Transactional
    public Product decreaseStock(Integer id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("减少数量必须大于0");
        }

        Product product = getProductById(id);
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("库存不足，当前库存: " + product.getStock());
        }

        product.setStock(product.getStock() - quantity);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * 更新商品价格
     */
    @Transactional
    public Product updateProductPrice(Integer id, BigDecimal newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("价格不能为空");
        }
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("价格必须大于0");
        }

        Product product = getProductById(id);
        product.setPrice(newPrice);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * 更新商品评分
     */
    @Transactional
    public Product updateProductRating(Integer id, Double newRating) {
        if (newRating == null) {
            throw new IllegalArgumentException("评分不能为空");
        }
        if (newRating < 0 || newRating > 5) {
            throw new IllegalArgumentException("评分必须在0-5之间");
        }

        Product product = getProductById(id);
        product.setRating(newRating);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    // ==================== 删除相关方法 ====================

    /**
     * 删除单个商品
     */
    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
    }

    /**
     * 批量删除商品
     */
    @Transactional
    public void deleteProducts(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID列表不能为空");
        }

        // 检查所有商品是否存在
        List<Product> existingProducts = productRepository.findByIdIn(ids);
        if (existingProducts.size() != ids.size()) {
            throw new ResourceNotFoundException("部分商品不存在");
        }

        productRepository.deleteAllById(ids);
    }

    // ==================== 业务逻辑方法 ====================

    /**
     * 检查库存是否充足
     */
    public boolean checkStock(Integer productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        Product product = getProductById(productId);
        return product.getStock() >= quantity;
    }

    /**
     * 获取商品库存数量
     */
    public Integer getProductStock(Integer productId) {
        Product product = getProductById(productId);
        return product.getStock();
    }

    /**
     * 获取商品统计信息
     */
    public Map<String, Object> getProductStats() {
        List<Product> products = productRepository.findAll();

        long totalStock = 0;
        long outOfStockCount = 0;
        long inStockCount = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal averagePrice = BigDecimal.ZERO;
        double averageRating = 0;

        for (Product product : products) {
            totalStock += product.getStock();

            // 计算总价值：价格 * 库存
            BigDecimal productValue = product.getPrice().multiply(BigDecimal.valueOf(product.getStock()));
            totalValue = totalValue.add(productValue);

            averageRating += product.getRating();

            if (product.getStock() <= 0) {
                outOfStockCount++;
            } else {
                inStockCount++;
            }
        }

        if (!products.isEmpty() && totalStock > 0) {
            // 计算平均价格
            averagePrice = totalValue.divide(BigDecimal.valueOf(totalStock), 2, java.math.RoundingMode.HALF_UP);
            averageRating = averageRating / products.size();
        }

        return Map.of(
                "totalProducts", products.size(),
                "totalStock", totalStock,
                "inStockCount", inStockCount,
                "outOfStockCount", outOfStockCount,
                "totalValue", totalValue.setScale(2, java.math.RoundingMode.HALF_UP),
                "averagePrice", averagePrice,
                "averageRating", String.format("%.2f", averageRating)
        );
    }

    /**
     * 根据品牌统计商品
     */
    public Map<String, Object> getStatsByBrand() {
        List<Product> products = productRepository.findAll();

        // 按品牌分组统计
        Map<String, Long> countByBrand = new HashMap<>();
        Map<String, Long> stockByBrand = new HashMap<>();
        Map<String, BigDecimal> avgPriceByBrand = new HashMap<>();

        for (Product product : products) {
            String brand = product.getBrand() != null ? product.getBrand() : "未知品牌";

            countByBrand.put(brand, countByBrand.getOrDefault(brand, 0L) + 1);
            stockByBrand.put(brand, stockByBrand.getOrDefault(brand, 0L) + product.getStock());

            // 计算平均价格（按品牌）
            if (!avgPriceByBrand.containsKey(brand)) {
                avgPriceByBrand.put(brand, product.getPrice());
            } else {
                long count = countByBrand.get(brand);
                BigDecimal currentTotal = avgPriceByBrand.get(brand).multiply(BigDecimal.valueOf(count - 1));
                BigDecimal newTotal = currentTotal.add(product.getPrice());
                BigDecimal newAvg = newTotal.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
                avgPriceByBrand.put(brand, newAvg);
            }
        }

        return Map.of(
                "countByBrand", countByBrand,
                "stockByBrand", stockByBrand,
                "avgPriceByBrand", avgPriceByBrand
        );
    }

    // ==================== 验证方法 ====================

    /**
     * 验证商品数据
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("商品不能为空");
        }

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }

        if (product.getPrice() == null) {
            throw new IllegalArgumentException("商品价格不能为空");
        }

        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("商品价格必须大于0");
        }

        // 名称长度限制
        if (product.getName().length() > 100) {
            throw new IllegalArgumentException("商品名称不能超过100个字符");
        }

        // 品牌长度限制
        if (product.getBrand() != null && product.getBrand().length() > 100) {
            throw new IllegalArgumentException("品牌名称不能超过100个字符");
        }

        // 价格范围限制
        if (product.getPrice().compareTo(BigDecimal.valueOf(1000000)) > 0) {
            throw new IllegalArgumentException("商品价格不能超过1000000");
        }

        // 库存范围限制
        if (product.getStock() != null && product.getStock() > 1000000) {
            throw new IllegalArgumentException("库存数量不能超过1000000");
        }

        // 评分范围验证
        if (product.getRating() != null) {
            if (product.getRating() < 0 || product.getRating() > 5) {
                throw new IllegalArgumentException("评分必须在0-5之间");
            }
        }
    }

    /**
     * 检查商品是否存在
     */
    public boolean existsById(Integer id) {
        return productRepository.existsById(id);
    }

    /**
     * 获取商品总数
     */
    public long getProductCount() {
        return productRepository.count();
    }

    /**
     * 获取低库存商品（库存少于指定阈值）
     */
    public List<Product> getLowStockProducts(Integer threshold) {
        if (threshold == null || threshold < 0) {
            throw new IllegalArgumentException("阈值必须大于等于0");
        }

        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .filter(product -> product.getStock() < threshold)
                .collect(Collectors.toList());
    }

    /**
     * 获取热门商品（按评分排序）
     */
    public List<Product> getTopRatedProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10个
        }

        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取最新商品（按创建时间排序）
     */
    public List<Product> getLatestProducts(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认返回10个
        }

        List<Product> allProducts = productRepository.findAll();
        return allProducts.stream()
                .sorted((p1, p2) -> {
                    if (p1.getCreatedAt() == null || p2.getCreatedAt() == null) {
                        return 0;
                    }
                    return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
}