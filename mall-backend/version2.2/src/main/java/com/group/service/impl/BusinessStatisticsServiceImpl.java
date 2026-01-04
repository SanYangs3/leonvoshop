package com.group.service.impl;

import com.group.mapper.BusinessStatisticsMapper;
import com.group.service.BusinessStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class BusinessStatisticsServiceImpl implements BusinessStatisticsService {

    @Autowired
    private BusinessStatisticsMapper businessStatisticsMapper;

    @Override
    public Map<String, Object> getBusinessStats(Integer bid) {
        Map<String, Object> stats = new HashMap<>();

        // 获取真实数据
        Integer todayOrderCount = getTodayOrderCount(bid);
        BigDecimal todaySales = getTodaySales(bid);
        Integer productCount = getProductCount(bid);
        Integer lowStockProductCount = getLowStockProductCount(bid);
        Integer pendingOrderCount = getPendingOrderCount(bid);
        BigDecimal totalSales = businessStatisticsMapper.getTotalSales(bid);
        Integer totalOrderCount = businessStatisticsMapper.countAllOrders(bid);

        stats.put("todayOrderCount", todayOrderCount);
        stats.put("todaySales", todaySales);
        stats.put("productCount", productCount);
        stats.put("lowStockProductCount", lowStockProductCount);
        stats.put("pendingOrderCount", pendingOrderCount);
        stats.put("totalSales", totalSales);
        stats.put("totalOrderCount", totalOrderCount);

        return stats;
    }

    @Override
    public Integer getTodayOrderCount(Integer bid) {
        Integer count = businessStatisticsMapper.countTodayOrders(bid);
        return count != null ? count : 0;
    }

    @Override
    public BigDecimal getTodaySales(Integer bid) {
        BigDecimal sales = businessStatisticsMapper.getTodaySales(bid);
        return sales != null ? sales : BigDecimal.ZERO;
    }

    @Override
    public Integer getProductCount(Integer bid) {
        Integer count = businessStatisticsMapper.countProducts(bid);
        return count != null ? count : 0;
    }

    @Override
    public Integer getLowStockProductCount(Integer bid) {
        Integer count = businessStatisticsMapper.countLowStockProducts(bid);
        return count != null ? count : 0;
    }

    @Override
    public Integer getPendingOrderCount(Integer bid) {
        Integer count = businessStatisticsMapper.countPendingOrders(bid);
        return count != null ? count : 0;
    }
}