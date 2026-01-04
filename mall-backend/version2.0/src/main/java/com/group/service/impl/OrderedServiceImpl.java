package com.group.service.impl;

import com.group.entity.Ordered;
import com.group.mapper.OrderedMapper;
import com.group.service.OrderedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OrderedServiceImpl implements OrderedService {

    private final OrderedMapper orderedMapper;

    @Autowired
    public OrderedServiceImpl(OrderedMapper orderedMapper) {
        this.orderedMapper = orderedMapper;
    }

    @Override
    public List<Ordered> getOrdersByUserId(Long uid) {
        return orderedMapper.getOrdersByUserId(uid);
    }

    @Override
    public Integer countAllOrders() {
        return orderedMapper.countAllOrders();
    }

    @Override
    public Integer countTodayOrders() {
        return orderedMapper.countTodayOrders();
    }

    @Override
    public Integer countYesterdayOrders() {
        return orderedMapper.countYesterdayOrders();
    }

    @Override
    public BigDecimal getTotalSales() {
        BigDecimal sales = orderedMapper.getTotalSales();
        return sales != null ? sales : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTodaySales() {
        BigDecimal sales = orderedMapper.getTodaySales();
        return sales != null ? sales : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getYesterdaySales() {
        BigDecimal sales = orderedMapper.getYesterdaySales();
        return sales != null ? sales : BigDecimal.ZERO;
    }

    @Override
    public List<Ordered> getAllOrders() {
        return orderedMapper.getAllOrders();
    }

    @Override
    public Ordered getOrderById(Integer oid) {
        return orderedMapper.getOrderById(oid);
    }

    @Override
    public List<Map<String, Object>> countOrdersByProvince() {
        return orderedMapper.countOrdersByProvince();
    }

    @Override
    public List<Map<String, Object>> getSalesByProvince() {
        return orderedMapper.getSalesByProvince();
    }

    @Override
    public List<Ordered> getOrdersByProvince(String province) {
        return orderedMapper.getOrdersByProvince(province);
    }

    @Override
    public List<Map<String, Object>> countTodayOrdersByProvince() {
        return orderedMapper.countTodayOrdersByProvince();
    }

    @Override
    public List<Map<String, Object>> getTodaySalesByProvince() {
        return orderedMapper.getTodaySalesByProvince();
    }
}