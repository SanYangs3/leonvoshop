package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 1. 根据商品名称模糊搜索
    List<Product> findByNameContaining(String keyword);

    // 2. 根据品牌搜索
    List<Product> findByBrand(String brand);

    // 3. 根据价格范围搜索 - 使用BigDecimal
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // 4. 根据名称和品牌组合搜索
    List<Product> findByNameContainingAndBrand(String name, String brand);

    // 5. 根据库存状态搜索（有库存）
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findInStockProducts();

    // 6. 根据评分搜索（评分高于指定值）
    List<Product> findByRatingGreaterThanEqual(Double minRating);

    // 7. 搜索商品（名称、品牌或描述）
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    // 8. 根据商品ID列表查询
    List<Product> findByIdIn(List<Integer> ids);
}