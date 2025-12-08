package com.lenovo.mall_backend.controller;

import com.lenovo.mall_backend.entity.Order;
import com.lenovo.mall_backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    // 1. 获取所有订单（最简单，不分页）
    @GetMapping
    public Map<String, Object> getAllOrders() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Order> orders = orderRepository.findAll();
            result.put("success", true);
            result.put("data", orders);
            result.put("count", orders.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取订单失败");
        }
        return result;
    }

    // 2. 搜索订单（按订单号或用户名）
    @GetMapping("/search")
    public Map<String, Object> searchOrders(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 先尝试按订单号查找
            Order orderByNo = orderRepository.findByOrderNo(keyword);

            if (orderByNo != null) {
                // 如果找到订单号匹配的
                result.put("success", true);
                result.put("data", List.of(orderByNo));  // 转换为列表
                result.put("count", 1);
            } else {
                // 如果没找到，按用户名查找
                List<Order> ordersByName = orderRepository.findByUserName(keyword);
                result.put("success", true);
                result.put("data", ordersByName);
                result.put("count", ordersByName.size());
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "搜索失败");
        }
        return result;
    }

    // 3. 按状态筛选
    @GetMapping("/status/{status}")
    public Map<String, Object> getOrdersByStatus(@PathVariable String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Order> orders = orderRepository.findByStatus(status);
            result.put("success", true);
            result.put("data", orders);
            result.put("count", orders.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取订单失败");
        }
        return result;
    }

    // 4. 获取统计信息
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Order> allOrders = orderRepository.findAll();

            // 统计各状态数量
            int total = allOrders.size();
            int pending = 0, processing = 0, shipped = 0, completed = 0, cancelled = 0;
            double totalAmount = 0;

            for (Order order : allOrders) {
                switch (order.getStatus()) {
                    case "pending": pending++; break;
                    case "processing": processing++; break;
                    case "shipped": shipped++; break;
                    case "completed":
                        completed++;
                        totalAmount += order.getTotalAmount();
                        break;
                    case "cancelled": cancelled++; break;
                }
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("pending", pending);
            stats.put("processing", processing);
            stats.put("shipped", shipped);
            stats.put("completed", completed);
            stats.put("cancelled", cancelled);
            stats.put("totalAmount", totalAmount);

            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计失败");
        }
        return result;
    }
}