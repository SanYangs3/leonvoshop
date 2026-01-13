package com.group.service.impl;

import com.group.entity.Order;
import com.group.mapper.OrderMapper;
import com.group.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    /*
    根据用户id获取用户订单
     */

    @Override
    public Order getOrderByUserId(Long uid) {
        return orderMapper.getOrderByUserId(uid);
    }
}
