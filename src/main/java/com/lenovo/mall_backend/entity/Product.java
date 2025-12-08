package com.lenovo.mall_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Double price;
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    // 新增字段
    private String brand;           // 品牌
    private Integer stock;          // 库存
    private String specifications;  // 详细规格
    private Double rating;          // 评分

    // 时间字段
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间

    // 构造方法
    public Product() {}
}