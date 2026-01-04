// 商家支付请求实体类，商家花钱张商品评分
package com.group.entity;

import lombok.Data;

@Data
public class PaymentRequest {
    private Integer bid;      // 商家ID
    private Integer pid;      // 商品ID
    private Integer amount;   // 支付金额：200, 500, 1000
}