package com.lenovo.mall_backend.entity;

import lombok.Data;

/**
 * 订单项实体类
 * 用于表示订单中的商品项
 */
@Data
public class OrderItem {
    private Integer productId;     // 商品ID
    private String productName;   // 商品名称
    private Double price;         // 商品单价
    private Integer quantity;     // 购买数量
    private Double subtotal;      // 小计金额（price * quantity）

    // 计算小计
    public Double getSubtotal() {
        if (price != null && quantity != null) {
            return price * quantity;
        }
        return 0.0;
    }

    public OrderItem() {}

    public OrderItem(Integer productId, String productName, Double price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }
}