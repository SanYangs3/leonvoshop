package com.group.service;

import java.math.BigDecimal;
import java.util.Map;

public interface BusinessStatisticsService {
    /*
    获取商家统计总览（所有数据）
    */
    Map<String, Object> getBusinessStats(Integer bid);

    /*
    获取商家今日订单数
    */
    Integer getTodayOrderCount(Integer bid);

    /*
    获取商家今日销售额
    */
    BigDecimal getTodaySales(Integer bid);

    /*
    获取商家商品总数
    */
    Integer getProductCount(Integer bid);

    /*
    获取商家低库存商品数（库存 <= 5）
    */
    Integer getLowStockProductCount(Integer bid);

    /*
    获取商家待发货订单数（status=0）
    */
    Integer getPendingOrderCount(Integer bid);

    /*
    获取商家总销售额（可选）
    */
    default BigDecimal getTotalSales(Integer bid) {
        return BigDecimal.ZERO; // 可以在实现类中重写
    }

    /*
    获取商家总订单数（可选）
    */
    default Integer getTotalOrderCount(Integer bid) {
        return 0; // 可以在实现类中重写
    }
}