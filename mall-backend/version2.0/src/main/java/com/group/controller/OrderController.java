package com.group.controller;

import com.group.entity.Order;
import com.group.entity.Result;
import com.group.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/order/")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*
    根据用户id获取用户订单
     */
    @GetMapping("{uid}")
    public Result<Order> getOrderByUserId(@PathVariable Long uid){
        log.info("根据用户id获取用户订单,id:{}",uid);
        Order order = orderService.getOrderByUserId(uid);
        return Result.success(order);
    }
}


