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
}
