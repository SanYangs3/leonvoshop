package com.group.entity;
/*
用户订单实体类
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    // 订单ID
    private Integer oid;

    // 状态：0待发货、1已发货、2已收货、3取消订单
    private Integer status;

    // 下单时间
    private String orderTime;

    // 商品件数
    private Integer amount;

    // 实付款
    private Float money;

    // 商品ID
    private Integer pid;

    // 用户ID
    private Integer uid;

    // 商家ID
    private Integer bid;

    // 收货地址
    private String address;

    // 收件人昵称
    private String momo;

    // 收件人电话
    private String phone;

    // 备注
    private String remark;
}
