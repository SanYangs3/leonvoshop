// 商家订单控制器
package com.group.controller;

import com.group.entity.Ordered;
import com.group.entity.Result;
import com.group.service.BusinessOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/orders")
public class BusinessOrderController {

    @Autowired
    private BusinessOrderService businessOrderService;

    /*
    功能4：商家获取自己管辖范围内所有商品的所有订单（不显示status=3的订单）
     */
    @GetMapping("/my/{bid}")
    public Result<List<Ordered>> getMyOrders(@PathVariable Integer bid) {
        try {
            List<Ordered> orders = businessOrderService.getOrdersByBusinessId(bid);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error("获取订单失败：" + e.getMessage());
        }
    }

    /*
    获取商家待发货订单（status=0）
     */
    @GetMapping("/my/{bid}/pending")
    public Result<List<Ordered>> getMyPendingOrders(@PathVariable Integer bid) {
        try {
            List<Ordered> orders = businessOrderService.getPendingOrdersByBusinessId(bid);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error("获取待发货订单失败：" + e.getMessage());
        }
    }

    /*
    功能5：商家点击发货（改变status=1）
     */
    @PutMapping("/ship/{bid}/{oid}")
    public Result<String> shipOrder(@PathVariable Integer bid, @PathVariable Integer oid) {
        try {
            boolean success = businessOrderService.shipOrder(bid, oid);
            if (success) {
                return Result.success("发货成功");
            } else {
                return Result.error("发货失败：订单不存在、不属于您或状态不正确");
            }
        } catch (Exception e) {
            return Result.error("发货失败：" + e.getMessage());
        }
    }
}