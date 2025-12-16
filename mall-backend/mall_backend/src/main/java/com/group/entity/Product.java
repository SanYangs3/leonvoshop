
package com.group.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Product {
    private Integer pid;           // 商品ID
    private String name;          // 商品名称
    private BigDecimal price;     // 价格
    private String description;   // 描述
    private String cpu;           // CPU
    private String gpu;           // GPU
    private String storage;       // 存储
    private String size;          // 尺寸
    private String type;          // 类型
    private String picture;       // 图片
    private Integer stock;        // 库存
    private Integer rating;       // 评分
}