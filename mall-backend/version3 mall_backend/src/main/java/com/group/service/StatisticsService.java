package com.group.service;

import com.group.entity.vo.ComparisonVO;
import com.group.entity.vo.SalesComparisonVO;
import java.math.BigDecimal;
import java.util.Map;

public interface StatisticsService {
    /*
    获取所有商品数
     */
    Integer getProductCount();

    /*
    获取所有订单数
     */
    Integer getOrderCount();

    /*
    获取今日订单数
     */
    Integer getTodayOrderCount();

    /*
    获取昨天订单数
     */
    Integer getYesterdayOrderCount();

    /*
    获取所有订单总的销售额
     */
    BigDecimal getTotalSales();

    /*
    获取今日销售额
     */
    BigDecimal getTodaySales();

    /*
    获取昨天销售额
     */
    BigDecimal getYesterdaySales();

    /*
   获取今日低库存商品数
    */
    Integer getLowStockProductCount();

    /*
    获取统计总览（包含低库存商品数）
     */
    Map<String, Object> getDashboardStats();

    /*
    5. 获取昨天和今天的订单数对比（包含百分比）
     */
    ComparisonVO getOrderCountComparison();

    /*
    8. 获取昨天和今天的销售额对比（包含百分比）
     */
    SalesComparisonVO getSalesComparison();

    /*
    9. 获取昨天和今天的商品数对比（包含百分比）
     */
    ComparisonVO getProductCountComparison();
}