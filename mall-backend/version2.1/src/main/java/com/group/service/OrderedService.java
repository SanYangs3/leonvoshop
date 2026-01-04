package com.group.service;

import com.group.entity.Ordered;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderedService {
    /*
    根据用户id获取用户订单
     */
    List<Ordered> getOrdersByUserId(Long uid);

    /*
    获取所有订单数
     */
    Integer countAllOrders();

    /*
    获取今日订单数
     */
    Integer countTodayOrders();

    /*
    获取昨天订单数
     */
    Integer countYesterdayOrders();

    /*
    获取所有订单总的销售额
     */
    BigDecimal getTotalSales();

    /*
    获取今天销售额
     */
    BigDecimal getTodaySales();

    /*
    获取昨天销售额
     */
    BigDecimal getYesterdaySales();

    /*
    获取所有订单信息
     */
    List<Ordered> getAllOrders();

    /*
    根据订单ID获取订单详情
     */
    Ordered getOrderById(Integer oid);

    /*
    按省份统计订单数量
     */
    List<Map<String, Object>> countOrdersByProvince();

    /*
    按省份统计销售额
     */
    List<Map<String, Object>> getSalesByProvince();

    /*
    查询某个省份的订单
     */
    List<Ordered> getOrdersByProvince(String province);

    /*
   获取今日各省份订单数
    */
    List<Map<String, Object>> countTodayOrdersByProvince();

    /*
    获取今日各省份销售额
     */
    List<Map<String, Object>> getTodaySalesByProvince();
}