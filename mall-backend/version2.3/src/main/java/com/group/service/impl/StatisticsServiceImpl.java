package com.group.service.impl;

import com.group.entity.vo.ComparisonVO;
import com.group.entity.vo.SalesComparisonVO;
import com.group.mapper.ProductMapper;
import com.group.service.OrderedService;
import com.group.service.ProductService;
import com.group.service.StatisticsService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final ProductMapper productMapper;
    private final OrderedService orderedService;
    private final ProductService productService;

    // 使用构造器注入（推荐方式）
    public StatisticsServiceImpl(ProductMapper productMapper,
                                 OrderedService orderedService,
                                 ProductService productService) {
        this.productMapper = productMapper;
        this.orderedService = orderedService;
        this.productService = productService;
    }

    @Override
    public Integer getProductCount() {
        return productMapper.countAllProducts();
    }

    @Override
    public Integer getOrderCount() {
        return orderedService.countAllOrders();
    }

    @Override
    public Integer getTodayOrderCount() {
        return orderedService.countTodayOrders();
    }

    @Override
    public Integer getYesterdayOrderCount() {
        return orderedService.countYesterdayOrders();
    }

    @Override
    public BigDecimal getTotalSales() {
        return orderedService.getTotalSales();
    }

    @Override
    public BigDecimal getTodaySales() {
        return orderedService.getTodaySales();
    }

    @Override
    public BigDecimal getYesterdaySales() {
        return orderedService.getYesterdaySales();
    }

    @Override
    public Integer getLowStockProductCount() {
        return productMapper.countLowStockProducts();
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("productCount", getProductCount());
        stats.put("orderCount", getOrderCount());
        stats.put("todayOrderCount", getTodayOrderCount());
        stats.put("totalSales", getTotalSales());
        stats.put("lowStockProductCount", getLowStockProductCount());

        // 新增对比数据
        stats.put("orderComparison", getOrderCountComparison());
        stats.put("salesComparison", getSalesComparison());
        stats.put("productComparison", getProductCountComparison());

        return stats;
    }

    @Override
    public ComparisonVO getOrderCountComparison() {
        Integer todayOrders = getTodayOrderCount();
        Integer yesterdayOrders = getYesterdayOrderCount();
        return new ComparisonVO(todayOrders, yesterdayOrders);
    }

    @Override
    public SalesComparisonVO getSalesComparison() {
        BigDecimal todaySales = getTodaySales();
        BigDecimal yesterdaySales = getYesterdaySales();
        return new SalesComparisonVO(todaySales, yesterdaySales);
    }

    @Override
    public ComparisonVO getProductCountComparison() {
        Integer todayProducts = productService.countTodayProducts();
        Integer yesterdayProducts = productService.countYesterdayProducts();
        return new ComparisonVO(todayProducts, yesterdayProducts);
    }
}