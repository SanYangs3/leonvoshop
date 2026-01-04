package com.group.controller;

import com.group.entity.Result;
import com.group.service.BusinessStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/business/stats")
public class BusinessStatisticsController {

    @Autowired
    private BusinessStatisticsService businessStatisticsService;

    /*
    获取商家统计总览
    路径：/api/business/stats/{bid}
     */
    @GetMapping("/{bid}")
    public Result<Map<String, Object>> getBusinessStats(@PathVariable Integer bid) {
        try {
            Map<String, Object> stats = businessStatisticsService.getBusinessStats(bid);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取商家统计数据失败：" + e.getMessage());
        }
    }

    /*
    获取商家今日订单数
     */
    @GetMapping("/{bid}/today-orders")
    public Result<Integer> getTodayOrders(@PathVariable Integer bid) {
        Integer count = businessStatisticsService.getTodayOrderCount(bid);
        return Result.success(count);
    }

    /*
    获取商家今日销售额
     */
    @GetMapping("/{bid}/today-sales")
    public Result<BigDecimal> getTodaySales(@PathVariable Integer bid) {
        BigDecimal sales = businessStatisticsService.getTodaySales(bid);
        return Result.success(sales);
    }

    /*
    获取商家商品总数
     */
    @GetMapping("/{bid}/product-count")
    public Result<Integer> getProductCount(@PathVariable Integer bid) {
        Integer count = businessStatisticsService.getProductCount(bid);
        return Result.success(count);
    }

    /*
    获取商家低库存商品数（库存 <= 5）
     */
    @GetMapping("/{bid}/low-stock-count")
    public Result<Integer> getLowStockCount(@PathVariable Integer bid) {
        Integer count = businessStatisticsService.getLowStockProductCount(bid);
        return Result.success(count);
    }

    /*
    获取商家待发货订单数（status=0）
     */
    @GetMapping("/{bid}/pending-orders")
    public Result<Integer> getPendingOrders(@PathVariable Integer bid) {
        Integer count = businessStatisticsService.getPendingOrderCount(bid);
        return Result.success(count);
    }

    /*
    获取商家总销售额
     */
    @GetMapping("/{bid}/total-sales")
    public Result<BigDecimal> getTotalSales(@PathVariable Integer bid) {
        BigDecimal sales = businessStatisticsService.getTotalSales(bid);
        return Result.success(sales);
    }

    /*
    获取商家总订单数
     */
    @GetMapping("/{bid}/total-orders")
    public Result<Integer> getTotalOrders(@PathVariable Integer bid) {
        Integer count = businessStatisticsService.getTotalOrderCount(bid);
        return Result.success(count);
    }
}