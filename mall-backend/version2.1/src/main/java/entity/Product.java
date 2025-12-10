package com.lenovo.mall_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;            // 商品名称

    @Column(nullable = false)
    private BigDecimal price;       // 价格 - 使用BigDecimal更精确

    @Column(columnDefinition = "TEXT")
    private String description;     // 描述

    @Column(name = "image_url", length = 200)
    private String imageUrl;        // 图片链接

    @Column(length = 100)
    private String brand;           // 品牌

    @Column(nullable = false)
    private Integer stock = 0;      // 库存，默认0

    @Column(columnDefinition = "TEXT")
    private String specifications;  // 详细规格

    @Column
    private Double rating = 0.0;    // 评分，默认0.0

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // 确保默认值
        if (stock == null) {
            stock = 0;
        }
        if (rating == null) {
            rating = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 构造方法
    public Product() {}
}