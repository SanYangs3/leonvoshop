package com.group.controller;

import com.group.entity.Ordered;
import com.group.entity.OrderItem;
import com.group.entity.Result;
import com.group.entity.OrderLogistics;
import com.group.service.OrderedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Slf4j
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
     * 订单发货接口
     */
    @PutMapping("/ship/{oid}")
    public Result<String> shipOrder(@PathVariable Integer oid,
                                    @RequestBody(required = false) OrderLogistics logisticsBody,
                                    @RequestParam(required = false) String courier,
                                    @RequestParam(required = false) String trackingNumber,
                                    @RequestParam(required = false) String tracking_number,
                                    @RequestParam(required = false) String note) {
        log.info("收到发货请求 - OID: {}", oid);

        // 参数兼容处理
        String finalCourier = courier;
        String finalTracking = trackingNumber != null ? trackingNumber : tracking_number;
        String finalNote = note;

        if (logisticsBody != null) {
            if (finalCourier == null) finalCourier = logisticsBody.getLogisticsCompany();
            if (finalTracking == null) finalTracking = logisticsBody.getTrackingNumber();
        }

        // 简单校验
        if (finalCourier == null || finalTracking == null) {
            return Result.error("物流公司和运单号不能为空");
        }

        try {
            // 调用Service处理发货逻辑
            boolean success = orderedService.shipOrder(oid, finalCourier, finalTracking, finalNote);

            if (success) {
                log.info("发货成功 - OID: {}, 快递: {}, 单号: {}", oid, finalCourier, finalTracking);
                return Result.success("发货成功");
            } else {
                log.error("发货失败 - OID: {}", oid);
                return Result.error("发货失败");
            }
        } catch (Exception e) {
            log.error("发货失败 - OID: {}", oid, e);
            return Result.error("发货失败: " + e.getMessage());
        }
    }

    /**
     * 根据订单号获取物流信息
     */
    @GetMapping("/{oid}/logistics")
    public Result<OrderLogistics> getLogisticsByOrderId(@PathVariable Integer oid) {
        OrderLogistics logistics = orderedService.getLogisticsByOrderId(oid);
        if (logistics == null) {
            return Result.error("未找到该订单的物流信息");
        }
        return Result.success(logistics);
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

    /*
    获取本周每日订单数趋势
    */
    @GetMapping("/trend/weekly")
    public Result<List<Map<String, Object>>> getWeeklyOrderTrend() {
        List<Map<String, Object>> data = orderedService.countDailyOrdersThisWeek();
        return Result.success(data);
    }

    /*
    获取本月每日订单数趋势
    */
    @GetMapping("/trend/monthly")
    public Result<List<Map<String, Object>>> getMonthlyOrderTrend() {
        List<Map<String, Object>> data = orderedService.countDailyOrdersThisMonth();
        return Result.success(data);
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