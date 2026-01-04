package com.group.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItem {
    private Integer id;                 // 主键ID
    private Integer oid;                // 订单ID
    private Integer pid;                // 商品ID
    private String name;               // 商品名称
    private BigDecimal price;          // 商品单价
    private Integer quantity;          // 购买数量
    private Integer bid;               // 商家ID

    // 计算小计金额
    public BigDecimal getSubtotal() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // 关联信息（可选）
    private Product product;           // 商品信息
    private Business business;         // 商家信息
}