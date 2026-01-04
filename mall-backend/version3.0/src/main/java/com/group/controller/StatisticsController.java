package com.group.controller;

import com.group.entity.Result;
import com.group.entity.vo.ComparisonVO;
import com.group.entity.vo.SalesComparisonVO;
import com.group.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /*
    获取所有商品数
     */
    @GetMapping("/product-count")
    public Result<Integer> getProductCount() {
        Integer count = statisticsService.getProductCount();
        return Result.success(count);
    }

    /*
    获取所有订单数
     */
    @GetMapping("/order-count")
    public Result<Integer> getOrderCount() {
        Integer count = statisticsService.getOrderCount();
        return Result.success(count);
    }

    /*
    获取今日订单数
     */
    @GetMapping("/today-order-count")
    public Result<Integer> getTodayOrderCount() {
        Integer count = statisticsService.getTodayOrderCount();
        return Result.success(count);
    }

    /*
    获取昨天订单数
     */
    @GetMapping("/yesterday-order-count")
    public Result<Integer> getYesterdayOrderCount() {
        Integer count = statisticsService.getYesterdayOrderCount();
        return Result.success(count);
    }

    /*
    获取所有订单总的销售额
     */
    @GetMapping("/total-sales")
    public Result<BigDecimal> getTotalSales() {
        BigDecimal totalSales = statisticsService.getTotalSales();
        return Result.success(totalSales);
    }

    /*
    获取今日销售额
     */
    @GetMapping("/today-sales")
    public Result<BigDecimal> getTodaySales() {
        BigDecimal todaySales = statisticsService.getTodaySales();
        return Result.success(todaySales);
    }

    /*
    获取昨天销售额
     */
    @GetMapping("/yesterday-sales")
    public Result<BigDecimal> getYesterdaySales() {
        BigDecimal yesterdaySales = statisticsService.getYesterdaySales();
        return Result.success(yesterdaySales);
    }

    /*
   获取今日低库存商品数
    */
    @GetMapping("/low-stock-count")
    public Result<Integer> getLowStockProductCount() {
        Integer count = statisticsService.getLowStockProductCount();
        return Result.success(count);
    }

    /*
    5. 获取昨天和今天的订单数对比（包含百分比）
     */
    @GetMapping("/order-comparison")
    public Result<ComparisonVO> getOrderCountComparison() {
        ComparisonVO comparison = statisticsService.getOrderCountComparison();
        return Result.success(comparison);
    }

    /*
    8. 获取昨天和今天的销售额对比（包含百分比）
     */
    @GetMapping("/sales-comparison")
    public Result<SalesComparisonVO> getSalesComparison() {
        SalesComparisonVO comparison = statisticsService.getSalesComparison();
        return Result.success(comparison);
    }

    /*
    9. 获取昨天和今天的商品数对比（包含百分比）
     */
    @GetMapping("/product-comparison")
    public Result<ComparisonVO> getProductCountComparison() {
        ComparisonVO comparison = statisticsService.getProductCountComparison();
        return Result.success(comparison);
    }

    /*
    获取统计总览（包含所有统计数据）
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = statisticsService.getDashboardStats();
        return Result.success(stats);
    }
}