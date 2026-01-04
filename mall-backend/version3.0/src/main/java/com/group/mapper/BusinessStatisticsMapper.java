package com.group.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface BusinessStatisticsMapper {

    // 1. 统计商家今日订单数
    @Select("SELECT COUNT(DISTINCT o.oid) " +
            "FROM ordered o " +
            "INNER JOIN orderItem oi ON o.oid = oi.oid " +
            "WHERE oi.bid = #{bid} AND DATE(o.order_time) = CURDATE()")
    Integer countTodayOrders(@Param("bid") Integer bid);

    // 2. 统计商家今日销售额
    @Select("SELECT COALESCE(SUM(oi.subtotal), 0) " +
            "FROM ordered o " +
            "INNER JOIN orderItem oi ON o.oid = oi.oid " +
            "WHERE oi.bid = #{bid} AND DATE(o.order_time) = CURDATE()")
    BigDecimal getTodaySales(@Param("bid") Integer bid);

    // 3. 统计商家商品总数
    @Select("SELECT COUNT(*) FROM business_product WHERE bid = #{bid}")
    Integer countProducts(@Param("bid") Integer bid);

    // 4. 统计商家低库存商品数（库存 <= 5）
    @Select("SELECT COUNT(*) " +
            "FROM product p " +
            "INNER JOIN business_product bp ON p.pid = bp.pid " +
            "WHERE bp.bid = #{bid} AND p.stock <= 5")
    Integer countLowStockProducts(@Param("bid") Integer bid);

    // 5. 统计商家待发货订单数（status=0）
    @Select("SELECT COUNT(DISTINCT o.oid) " +
            "FROM ordered o " +
            "INNER JOIN orderItem oi ON o.oid = oi.oid " +
            "WHERE oi.bid = #{bid} AND o.status = 0")
    Integer countPendingOrders(@Param("bid") Integer bid);

    // 6. 统计商家总销售额（可选）
    @Select("SELECT COALESCE(SUM(oi.subtotal), 0) " +
            "FROM ordered o " +
            "INNER JOIN orderItem oi ON o.oid = oi.oid " +
            "WHERE oi.bid = #{bid}")
    BigDecimal getTotalSales(@Param("bid") Integer bid);

    // 7. 统计商家总订单数（可选）
    @Select("SELECT COUNT(DISTINCT o.oid) " +
            "FROM ordered o " +
            "INNER JOIN orderItem oi ON o.oid = oi.oid " +
            "WHERE oi.bid = #{bid}")
    Integer countAllOrders(@Param("bid") Integer bid);
}