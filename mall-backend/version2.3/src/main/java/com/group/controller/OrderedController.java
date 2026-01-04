package com.group.controller;

import com.group.entity.Ordered;
import com.group.entity.OrderItem;
import com.group.entity.Result;
import com.group.service.OrderedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderedController {

    private final OrderedService orderedService;

    @Autowired
    public OrderedController(OrderedService orderedService) {
        this.orderedService = orderedService;
    }

    /*
    获取所有订单数
     */
    @GetMapping("/count")
    public Result<Integer> countAllOrders() {
        Integer count = orderedService.countAllOrders();
        return Result.success(count);
    }

    /*
    获取今日订单数
     */
    @GetMapping("/today-count")
    public Result<Integer> countTodayOrders() {
        Integer count = orderedService.countTodayOrders();
        return Result.success(count);
    }

    /*
    获取昨天订单数
     */
    @GetMapping("/yesterday-count")
    public Result<Integer> countYesterdayOrders() {
        Integer count = orderedService.countYesterdayOrders();
        return Result.success(count);
    }

    /*
    获取所有订单总的销售额
     */
    @GetMapping("/total-sales")
    public Result<BigDecimal> getTotalSales() {
        BigDecimal totalSales = orderedService.getTotalSales();
        return Result.success(totalSales);
    }

    /*
    获取今日销售额
     */
    @GetMapping("/today-sales")
    public Result<BigDecimal> getTodaySales() {
        BigDecimal todaySales = orderedService.getTodaySales();
        return Result.success(todaySales);
    }

    /*
    获取昨天销售额
     */
    @GetMapping("/yesterday-sales")
    public Result<BigDecimal> getYesterdaySales() {
        BigDecimal yesterdaySales = orderedService.getYesterdaySales();
        return Result.success(yesterdaySales);
    }

    /*
    获取所有订单信息
     */
    @GetMapping
    public Result<List<Ordered>> getAllOrders() {
        List<Ordered> orders = orderedService.getAllOrders();
        return Result.success(orders);
    }

    /*
    根据订单ID获取订单详情
     */
    @GetMapping("/{oid}")
    public Result<Ordered> getOrderById(@PathVariable Integer oid) {
        Ordered order = orderedService.getOrderById(oid);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    /*
    根据用户ID获取订单
     */
    @GetMapping("/user/{uid}")
    public Result<List<Ordered>> getOrdersByUserId(@PathVariable Long uid) {
        List<Ordered> orders = orderedService.getOrdersByUserId(uid);
        return Result.success(orders);
    }

    /*
    按省份统计订单数量
     */
    @GetMapping("/statistics/by-province")
    public Result<List<Map<String, Object>>> countOrdersByProvince() {
        List<Map<String, Object>> data = orderedService.countOrdersByProvince();
        return Result.success(data);
    }

    /*
    按省份统计销售额
     */
    @GetMapping("/statistics/sales-by-province")
    public Result<List<Map<String, Object>>> getSalesByProvince() {
        List<Map<String, Object>> data = orderedService.getSalesByProvince();
        return Result.success(data);
    }

    /*
    查询某个省份的订单
     */
    @GetMapping("/province/{province}")
    public Result<List<Ordered>> getOrdersByProvince(@PathVariable String province) {
        List<Ordered> orders = orderedService.getOrdersByProvince(province);
        return Result.success(orders);
    }

    /*
   获取今日各省份订单数
    */
    @GetMapping("/today/statistics/by-province")
    public Result<List<Map<String, Object>>> countTodayOrdersByProvince() {
        List<Map<String, Object>> data = orderedService.countTodayOrdersByProvince();
        return Result.success(data);
    }

    /*
    获取今日各省份销售额
     */
    @GetMapping("/today/statistics/sales-by-province")
    public Result<List<Map<String, Object>>> getTodaySalesByProvince() {
        List<Map<String, Object>> data = orderedService.getTodaySalesByProvince();
        return Result.success(data);
    }

    /**
     * 用户购买商品生成订单
     */
    @PostMapping("/create")
    public Result<Integer> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Integer orderId = orderedService.createOrder(
                    request.getUid(),
                    request.getItems(),
                    request.getProvince(),
                    request.getReceiverAddress(),
                    request.getReceiverName(),
                    request.getReceiverPhone(),
                    request.getRemark()
            );
            return Result.success(orderId);
        } catch (Exception e) {
            return Result.error("创建订单失败：" + e.getMessage());
        }
    }

    /**
     * 用户确认收货
     */
    @PutMapping("/{oid}/confirm-receipt")
    public Result<Boolean> confirmReceipt(@PathVariable Integer oid, @RequestParam Integer uid) {
        try {
            boolean success = orderedService.confirmReceipt(oid, uid);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("确认收货失败");
            }
        } catch (Exception e) {
            return Result.error("确认收货失败：" + e.getMessage());
        }
    }

    /**
     * 用户取消订单
     */
    @PutMapping("/{oid}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Integer oid, @RequestParam Integer uid) {
        try {
            boolean success = orderedService.cancelOrder(oid, uid);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("取消订单失败");
            }
        } catch (Exception e) {
            return Result.error("取消订单失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单详情（包含订单项）
     */
    @GetMapping("/detail/{oid}")
    public Result<Ordered> getOrderDetail(@PathVariable Integer oid) {
        Ordered order = orderedService.getOrderDetailById(oid);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    // 创建订单请求类
    static class CreateOrderRequest {
        private Integer uid;
        private List<OrderItem> items;
        private String province;
        private String receiverAddress;
        private String receiverName;
        private String receiverPhone;
        private String remark;

        // getters and setters
        public Integer getUid() { return uid; }
        public void setUid(Integer uid) { this.uid = uid; }

        public List<OrderItem> getItems() { return items; }
        public void setItems(List<OrderItem> items) { this.items = items; }

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getReceiverAddress() { return receiverAddress; }
        public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}