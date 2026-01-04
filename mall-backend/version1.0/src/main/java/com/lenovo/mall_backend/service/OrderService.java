//package service;  // 修改包名以匹配您的目录结构
package com.lenovo.mall_backend.service;

import com.lenovo.mall_backend.entity.Order;
import com.lenovo.mall_backend.entity.OrderItem;
import com.lenovo.mall_backend.exception.ResourceNotFoundException;
import com.lenovo.mall_backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 订单服务类（修复版）
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ==================== 查询相关方法 ====================

    /**
     * 获取所有订单
     */
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        // 为每个订单设置订单项
        orders.forEach(this::parseOrderItems);
        return orders;
    }

    /**
     * 分页获取订单
     */
    public Page<Order> getOrdersByPage(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        // 为每个订单设置订单项
        orderPage.forEach(this::parseOrderItems);
        return orderPage;
    }

    /**
     * 根据ID获取订单
     */
    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在，ID: " + id));
        // 解析订单项
        parseOrderItems(order);
        return order;
    }

    /**
     * 根据订单编号获取订单
     */
    public Order getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("订单不存在，订单号: " + orderNumber));
        // 解析订单项
        parseOrderItems(order);
        return order;
    }

    /**
     * 根据用户ID获取订单
     */
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        // 为每个订单设置订单项
        orders.forEach(this::parseOrderItems);
        return orders;
    }

    /**
     * 根据状态获取订单
     */
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        // 为每个订单设置订单项
        orders.forEach(this::parseOrderItems);
        return orders;
    }

    /**
     * 根据用户ID和状态获取订单
     */
    public List<Order> getOrdersByUserIdAndStatus(Long userId, String status) {
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status);
        // 为每个订单设置订单项
        orders.forEach(this::parseOrderItems);
        return orders;
    }

    /**
     * 搜索订单（订单号、收货人、电话）
     */
    public List<Order> searchOrders(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            List<Order> orders = orderRepository.findAll();
            orders.forEach(this::parseOrderItems);
            return orders;
        }

        Specification<Order> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 订单号模糊搜索
            predicates.add(criteriaBuilder.like(root.get("orderNumber"), "%" + keyword + "%"));

            // 收货人姓名模糊搜索
            predicates.add(criteriaBuilder.like(root.get("receiverName"), "%" + keyword + "%"));

            // 收货人电话模糊搜索
            predicates.add(criteriaBuilder.like(root.get("receiverPhone"), "%" + keyword + "%"));

            // 订单备注模糊搜索
            predicates.add(criteriaBuilder.like(root.get("remark"), "%" + keyword + "%"));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

        List<Order> orders = orderRepository.findAll(specification);
        orders.forEach(this::parseOrderItems);
        return orders;
    }

    /**
     * 多条件查询订单
     */
    public List<Order> findOrdersByCriteria(String orderNumber, String status,
                                            Long userId, String receiverName,
                                            String receiverPhone, String startTime,
                                            String endTime) {
        Specification<Order> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(orderNumber)) {
                predicates.add(criteriaBuilder.like(root.get("orderNumber"), "%" + orderNumber + "%"));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }
            if (StringUtils.hasText(receiverName)) {
                predicates.add(criteriaBuilder.like(root.get("receiverName"), "%" + receiverName + "%"));
            }
            if (StringUtils.hasText(receiverPhone)) {
                predicates.add(criteriaBuilder.like(root.get("receiverPhone"), "%" + receiverPhone + "%"));
            }
            if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
                // 注意：这里假设时间字段是字符串格式
                predicates.add(criteriaBuilder.between(root.get("orderTime"), startTime, endTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<Order> orders = orderRepository.findAll(specification);
        orders.forEach(this::parseOrderItems);
        return orders;
    }

    /**
     * 获取用户最近订单
     */
    public List<Order> getRecentOrdersByUserId(Long userId, Integer limit) {
        List<Order> orders = orderRepository.findByUserId(userId);
        // 按时间倒序排序
        orders.sort((o1, o2) -> {
            try {
                LocalDateTime t1 = LocalDateTime.parse(o1.getOrderTime(), dateTimeFormatter);
                LocalDateTime t2 = LocalDateTime.parse(o2.getOrderTime(), dateTimeFormatter);
                return t2.compareTo(t1);
            } catch (Exception e) {
                return 0;
            }
        });

        // 限制数量
        if (limit != null && limit > 0 && orders.size() > limit) {
            orders = orders.subList(0, limit);
        }

        // 解析订单项
        orders.forEach(this::parseOrderItems);
        return orders;
    }

    // ==================== 新增相关方法 ====================

    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(Order order) {
        // 验证订单数据
        validateOrder(order);

        // 设置订单时间
        if (order.getOrderTime() == null) {
            order.setOrderTime(LocalDateTime.now().format(dateTimeFormatter));
        }

        // 生成订单编号
        if (order.getOrderNumber() == null) {
            order.setOrderNumber(generateOrderNumber());
        }

        // 设置默认状态
        if (order.getStatus() == null) {
            order.setStatus("0");  // 待支付
        }

        // 计算订单总金额
        order.calculateTotalAmount();

        // 将订单项转换为JSON字符串并保存到gwc字段
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            String gwcJson = convertOrderItemsToJson(order.getOrderItems());
            order.setGwc(gwcJson);
        }

        // 保存订单
        Order savedOrder = orderRepository.save(order);

        // 重新解析订单项
        parseOrderItems(savedOrder);

        return savedOrder;
    }

    /**
     * 批量创建订单
     */
    @Transactional
    public List<Order> createOrders(List<Order> orders) {
        List<Order> createdOrders = new ArrayList<>();
        for (Order order : orders) {
            try {
                Order createdOrder = createOrder(order);
                createdOrders.add(createdOrder);
            } catch (Exception e) {
                // 记录错误，继续处理其他订单
                System.err.println("创建订单失败: " + e.getMessage());
            }
        }
        return createdOrders;
    }

    // ==================== 更新相关方法 ====================

    /**
     * 更新订单信息（只能更新部分字段）
     */
    @Transactional
    public Order updateOrder(Long id, Order orderDetails) {
        Order existingOrder = getOrderById(id);

        // 只能更新以下字段
        if (StringUtils.hasText(orderDetails.getReceiverName())) {
            existingOrder.setReceiverName(orderDetails.getReceiverName());
        }
        if (StringUtils.hasText(orderDetails.getReceiverPhone())) {
            existingOrder.setReceiverPhone(orderDetails.getReceiverPhone());
        }
        if (StringUtils.hasText(orderDetails.getReceiverAddress())) {
            existingOrder.setReceiverAddress(orderDetails.getReceiverAddress());
        }
        if (StringUtils.hasText(orderDetails.getRemark())) {
            existingOrder.setRemark(orderDetails.getRemark());
        }

        Order updatedOrder = orderRepository.save(existingOrder);
        parseOrderItems(updatedOrder);
        return updatedOrder;
    }

    /**
     * 更新订单状态
     */
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("订单状态无效: " + status);
        }

        Order order = getOrderById(id);
        String oldStatus = order.getStatus();
        order.setStatus(status);

        // 如果订单取消，恢复库存
        if ("4".equals(status) && !"4".equals(oldStatus)) {
            // restoreStockForOrder(order);
        }

        // 如果订单完成，可以更新用户积分等
        if ("3".equals(status) && !"3".equals(oldStatus)) {
            // updateUserScore(order);
        }

        Order updatedOrder = orderRepository.save(order);
        parseOrderItems(updatedOrder);
        return updatedOrder;
    }

    /**
     * 发货订单
     */
    @Transactional
    public Order shipOrder(Long id, String shippingNumber) {
        Order order = getOrderById(id);

        if (!"1".equals(order.getStatus())) {
            throw new IllegalArgumentException("只有已支付的订单可以发货");
        }

        order.setStatus("2");  // 已发货
        // 可以将快递单号添加到备注中
        if (shippingNumber != null) {
            String remark = order.getRemark() != null ? order.getRemark() : "";
            order.setRemark(remark + " 快递单号:" + shippingNumber);
        }

        Order updatedOrder = orderRepository.save(order);
        parseOrderItems(updatedOrder);
        return updatedOrder;
    }

    /**
     * 完成订单
     */
    @Transactional
    public Order completeOrder(Long id) {
        Order order = getOrderById(id);

        if (!"2".equals(order.getStatus())) {
            throw new IllegalArgumentException("只有已发货的订单可以完成");
        }

        order.setStatus("3");  // 已完成
        Order updatedOrder = orderRepository.save(order);
        parseOrderItems(updatedOrder);
        return updatedOrder;
    }

    /**
     * 取消订单
     */
    @Transactional
    public Order cancelOrder(Long id, String reason) {
        Order order = getOrderById(id);

        // 只能取消待支付或已支付的订单
        if (!("0".equals(order.getStatus()) || "1".equals(order.getStatus()))) {
            throw new IllegalArgumentException("当前状态的订单不可取消");
        }

        order.setStatus("4");  // 已取消
        // 将取消原因添加到备注中
        if (reason != null) {
            String remark = order.getRemark() != null ? order.getRemark() : "";
            order.setRemark(remark + " 取消原因:" + reason);
        }

        Order updatedOrder = orderRepository.save(order);
        parseOrderItems(updatedOrder);
        return updatedOrder;
    }

    /**
     * 支付订单
     */
    @Transactional
    public Order payOrder(Long id) {
        Order order = getOrderById(id);

        if (!"0".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单当前状态不可支付");
        }

        order.setStatus("1");  // 已支付
        Order updatedOrder = orderRepository.save(order);
        parseOrderItems(updatedOrder);
        return updatedOrder;
    }

    // ==================== 删除相关方法 ====================

    /**
     * 删除订单
     */
    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);

        // 只能删除已取消的订单
        if (!"4".equals(order.getStatus())) {
            throw new IllegalArgumentException("只能删除已取消的订单");
        }

        orderRepository.delete(order);
    }

    /**
     * 批量删除订单
     */
    @Transactional
    public void deleteOrders(List<Long> ids) {
        for (Long id : ids) {
            try {
                deleteOrder(id);
            } catch (Exception e) {
                // 记录错误，继续删除其他订单
                System.err.println("删除订单失败 ID=" + id + ": " + e.getMessage());
            }
        }
    }

    // ==================== 业务逻辑方法 ====================

    /**
     * 获取订单统计信息
     */
    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();

        // 订单总数
        long totalOrders = orderRepository.count();
        stats.put("totalOrders", totalOrders);

        // 按状态统计
        long pendingCount = orderRepository.countByStatus("0");
        long paidCount = orderRepository.countByStatus("1");
        long shippedCount = orderRepository.countByStatus("2");
        long completedCount = orderRepository.countByStatus("3");
        long cancelledCount = orderRepository.countByStatus("4");

        stats.put("pendingCount", pendingCount);
        stats.put("paidCount", paidCount);
        stats.put("shippedCount", shippedCount);
        stats.put("completedCount", completedCount);
        stats.put("cancelledCount", cancelledCount);

        // 总销售额
        Double totalSales = orderRepository.sumTotalSales();
        stats.put("totalSales", totalSales != null ? String.format("%.2f", totalSales) : "0.00");

        // 今日订单数
        long todayOrders = orderRepository.countTodayOrders();
        stats.put("todayOrders", todayOrders);

        // 今日销售额
        Double todaySales = orderRepository.sumTodaySales();
        stats.put("todaySales", todaySales != null ? String.format("%.2f", todaySales) : "0.00");

        // 平均订单金额
        double avgOrderAmount = totalOrders > 0 ?
                (totalSales != null ? totalSales / totalOrders : 0) : 0;
        stats.put("avgOrderAmount", String.format("%.2f", avgOrderAmount));

        return stats;
    }

    /**
     * 获取销售额统计
     */
    public Map<String, Object> getSalesStats(String startDate, String endDate) {
        Map<String, Object> stats = new HashMap<>();

        // 这里需要实现根据日期范围查询的逻辑
        // 由于时间字段是字符串，我们需要特殊处理
        List<Order> allOrders = orderRepository.findAll();

        double totalSales = 0;
        int orderCount = 0;

        try {
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", dateTimeFormatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", dateTimeFormatter);

            for (Order order : allOrders) {
                try {
                    LocalDateTime orderTime = LocalDateTime.parse(order.getOrderTime(), dateTimeFormatter);

                    if ((orderTime.isAfter(start) || orderTime.isEqual(start)) &&
                            (orderTime.isBefore(end) || orderTime.isEqual(end))) {

                        // 排除已取消的订单
                        if (!"4".equals(order.getStatus())) {
                            try {
                                double amount = Double.parseDouble(order.getAmount());
                                totalSales += amount;
                                orderCount++;
                            } catch (NumberFormatException e) {
                                // 金额格式错误，跳过
                            }
                        }
                    }
                } catch (Exception e) {
                    // 时间解析错误，跳过此订单
                }
            }
        } catch (Exception e) {
            // 日期范围解析错误
        }

        stats.put("totalSales", String.format("%.2f", totalSales));
        stats.put("orderCount", orderCount);
        stats.put("averageOrderValue", orderCount > 0 ? String.format("%.2f", totalSales / orderCount) : "0.00");

        return stats;
    }

    /**
     * 获取用户订单统计
     */
    public Map<String, Object> getUserOrderStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 用户订单总数
        long userOrderCount = orderRepository.countByUserId(userId);
        stats.put("userOrderCount", userOrderCount);

        // 用户总消费金额
        List<Order> userOrders = orderRepository.findByUserId(userId);
        double totalSpent = userOrders.stream()
                .filter(o -> !"4".equals(o.getStatus()))  // 排除已取消的订单
                .mapToDouble(order -> {
                    try {
                        return Double.parseDouble(order.getAmount());
                    } catch (Exception e) {
                        return 0.0;
                    }
                })
                .sum();
        stats.put("totalSpent", String.format("%.2f", totalSpent));

        return stats;
    }

    // ==================== 工具方法 ====================

    /**
     * 解析订单项（从gwc字段解析JSON）
     */
    private void parseOrderItems(Order order) {
        try {
            String gwcJson = order.getGwc();
            if (StringUtils.hasText(gwcJson)) {
                // 手动解析JSON
                List<OrderItem> orderItems = parseOrderItemsFromJson(gwcJson);
                order.setOrderItems(orderItems);
            } else {
                order.setOrderItems(new ArrayList<>());
            }
        } catch (Exception e) {
            // 设置空列表
            order.setOrderItems(new ArrayList<>());
        }
    }

    /**
     * 从JSON字符串解析订单项
     */
    private List<OrderItem> parseOrderItemsFromJson(String json) {
        List<OrderItem> orderItems = new ArrayList<>();

        try {
            // 简单的手动解析JSON
            // 格式示例：[{"productId":1,"productName":"商品1","price":100.0,"quantity":2},...]
            String trimmed = json.trim();
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                String content = trimmed.substring(1, trimmed.length() - 1).trim();
                if (!content.isEmpty()) {
                    String[] items = content.split("\\},\\{");
                    for (String item : items) {
                        String cleanItem = item.trim();
                        if (cleanItem.startsWith("{")) {
                            cleanItem = cleanItem.substring(1);
                        }
                        if (cleanItem.endsWith("}")) {
                            cleanItem = cleanItem.substring(0, cleanItem.length() - 1);
                        }

                        OrderItem orderItem = parseOrderItem(cleanItem);
                        if (orderItem != null) {
                            orderItems.add(orderItem);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败，返回空列表
        }

        return orderItems;
    }

    /**
     * 解析单个订单项
     */
    private OrderItem parseOrderItem(String itemJson) {
        try {
            OrderItem item = new OrderItem();

            // 解析键值对
            String[] pairs = itemJson.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");

                    switch (key) {
                        case "productId":
                            item.setProductId(Integer.parseInt(value));
                            break;
                        case "productName":
                            item.setProductName(value);
                            break;
                        case "price":
                            item.setPrice(Double.parseDouble(value));
                            break;
                        case "quantity":
                            item.setQuantity(Integer.parseInt(value));
                            break;
                    }
                }
            }

            return item;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将订单项转换为JSON字符串
     */
    private String convertOrderItemsToJson(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return "[]";
        }

        // 手动生成JSON
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            jsonBuilder.append("{")
                    .append("\"productId\":").append(item.getProductId())
                    .append(",\"productName\":\"").append(item.getProductName()).append("\"")
                    .append(",\"price\":").append(item.getPrice())
                    .append(",\"quantity\":").append(item.getQuantity())
                    .append("}");

            if (i < orderItems.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        return jsonBuilder.toString();
    }

    /**
     * 验证订单数据
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("订单不能为空");
        }

        if (order.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("订单商品不能为空");
        }

        // 验证每个订单项
        for (OrderItem item : orderItems) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("商品ID不能为空");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("商品数量必须大于0");
            }
            if (item.getPrice() == null || item.getPrice() <= 0) {
                throw new IllegalArgumentException("商品价格必须大于0");
            }
        }

        // 验证收货信息
        if (!StringUtils.hasText(order.getReceiverName())) {
            throw new IllegalArgumentException("收货人姓名不能为空");
        }
        if (!StringUtils.hasText(order.getReceiverPhone())) {
            throw new IllegalArgumentException("收货人电话不能为空");
        }
        if (!StringUtils.hasText(order.getReceiverAddress())) {
            throw new IllegalArgumentException("收货地址不能为空");
        }
    }

    /**
     * 验证订单状态是否有效
     */
    private boolean isValidStatus(String status) {
        return Arrays.asList("0", "1", "2", "3", "4").contains(status);
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 900) + 100;  // 100-999
        return "ORD" + dateStr + random;
    }

    /**
     * 检查订单是否存在
     */
    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

    /**
     * 获取订单总数
     */
    public long getOrderCount() {
        return orderRepository.count();
    }
}