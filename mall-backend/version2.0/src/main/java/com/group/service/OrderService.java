package com.group.service;

import com.group.entity.Order;
import lombok.extern.slf4j.Slf4j;

public interface OrderService {

    /*
    根据用户id获取用户订单
     */
    Order getOrderByUserId(Long uid);

}
