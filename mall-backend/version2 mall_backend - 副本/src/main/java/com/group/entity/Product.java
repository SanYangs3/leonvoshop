
package com.group.entity;

import lombok.Data;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore; // 添加这行

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

    // 临时字段，仅用于接收上传时的商家ID，不存储到数据库
    @JsonIgnore  // 这个注解表示JSON序列化时忽略此字段
    private transient Integer bid; // transient关键字表示不序列化到数据库
}