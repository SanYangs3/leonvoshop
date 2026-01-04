package com.group.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
购物车实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    // 购物车ID
    private Integer cid;

    // 用户ID
    private Integer uid;

    // 商品ID
    private Integer pid;

    //商家ID
    private Integer bid;

    //商品数量
    private Integer quantity;

    //关联查询字段
    private Product product;
    private  Business business;
}
