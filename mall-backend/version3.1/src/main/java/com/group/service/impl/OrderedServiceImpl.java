package com.group.service.impl;

import com.group.entity.Ordered;
import com.group.entity.OrderItem;
import com.group.entity.Product;
import com.group.entity.OrderLogistics;
import com.group.mapper.OrderedMapper;
import com.group.mapper.OrderItemMapper;
import com.group.mapper.ProductMapper;
import com.group.mapper.OrderLogisticsMapper;
import com.group.service.OrderedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class OrderedServiceImpl implements OrderedService {

    private final OrderedMapper orderedMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final OrderLogisticsMapper orderLogisticsMapper;

    @Autowired
    public OrderedServiceImpl(OrderedMapper orderedMapper,
                              OrderItemMapper orderItemMapper,
                              ProductMapper productMapper,
                              OrderLogisticsMapper orderLogisticsMapper) {
        this.orderedMapper = orderedMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.orderLogisticsMapper = orderLogisticsMapper;
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

    /**
     * 创建订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createOrder(Integer uid, List<OrderItem> items,
                               String province, String receiverAddress,
                               String receiverName, String receiverPhone,
                               String remark) throws Exception {

        if (items == null || items.isEmpty()) {
            throw new Exception("订单商品列表不能为空");
        }

        // 1. 计算订单总金额并验证库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            // 验证商品信息
            Product product = productMapper.selectById(item.getPid());
            if (product == null) {
                throw new Exception("商品ID " + item.getPid() + " 不存在");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new Exception("商品 " + product.getName() + " 库存不足，当前库存：" + product.getStock());
            }

            // 设置商品名称和价格
            item.setName(product.getName());
            item.setPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            // 计算总金额（用于订单总金额）
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // 2. 创建订单
        Ordered order = new Ordered();
        order.setUid(uid);
        order.setStatus(1); // 待发货
        order.setOrderTime(new Date());
        order.setAmount(totalAmount);
        order.setProvince(province);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setRemark(remark);

        int result = orderedMapper.insertOrder(order);
        if (result <= 0) {
            throw new Exception("创建订单失败");
        }

        Integer orderId = order.getOid();

        // 3. 插入订单项
        for (OrderItem item : items) {
            item.setOid(orderId);
        }
        result = orderItemMapper.insertOrderItems(items);
        if (result <= 0) {
            throw new Exception("添加订单项失败");
        }

        // 4. 扣减库存
        for (OrderItem item : items) {
            result = productMapper.decreaseStock(item.getPid(), item.getQuantity());
            if (result <= 0) {
                throw new Exception("扣减商品 " + item.getName() + " 库存失败");
            }
        }

        return orderId;
    }

    /**
     * 确认收货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceipt(Integer oid, Integer uid) throws Exception {
        // 验证订单是否存在且属于该用户
        Ordered order = orderedMapper.getOrderByOidAndUid(oid, uid);
        if (order == null) {
            throw new Exception("订单不存在或不属于当前用户");
        }

        // 验证订单状态（只有已发货状态才能确认收货）
        if (order.getStatus() != 2) {
            throw new Exception("只有已发货的订单才能确认收货，当前状态：" + order.getStatusText());
        }

        // 更新订单状态为已收货
        int result = orderedMapper.updateOrderStatus(oid, 3);
        return result > 0;
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Integer oid, Integer uid) throws Exception {
        // 验证订单是否存在且属于该用户
        Ordered order = orderedMapper.getOrderByOidAndUid(oid, uid);
        if (order == null) {
            throw new Exception("订单不存在或不属于当前用户");
        }

        // 验证订单状态（只有待发货状态才能取消）
        if (order.getStatus() != 1) {
            throw new Exception("只有待发货的订单才能取消，当前状态：" + order.getStatusText());
        }

        // 获取订单项以恢复库存
        List<OrderItem> items = orderItemMapper.getItemsByOrderIdWithProduct(oid);

        // 更新订单状态为已取消
        int result = orderedMapper.updateOrderStatus(oid, 4);
        if (result <= 0) {
            return false;
        }

        // 恢复库存
        for (OrderItem item : items) {
            productMapper.increaseStock(item.getPid(), item.getQuantity());
        }

        return true;
    }

    /**
     * 获取订单详情（包含订单项）
     */
    @Override
    public Ordered getOrderDetailById(Integer oid) {
        Ordered order = orderedMapper.getOrderById(oid);
        if (order != null) {
            List<OrderItem> items = orderItemMapper.getItemsByOrderIdWithProduct(oid);
            order.setItems(items);

            // 获取物流信息并附加到订单对象
            try {
                OrderLogistics logistics = orderLogisticsMapper.selectByOrderId(oid);
                if (logistics != null) {
                    Map<String, Object> logisticsInfo = new HashMap<>();
                    logisticsInfo.put("logistics_company", logistics.getLogisticsCompany());
                    logisticsInfo.put("tracking_number", logistics.getTrackingNumber());
                    logisticsInfo.put("ship_time", logistics.getCreatedTime());
                    order.setLogistics(logisticsInfo);
                }
            } catch (Exception e) {
                System.err.println("获取物流信息失败: " + e.getMessage());
            }
        }
        return order;
    }

    @Override
    public List<Map<String, Object>> countDailyOrdersThisWeek() {
        return orderedMapper.countDailyOrdersThisWeek();
    }

    @Override
    public List<Map<String, Object>> countDailyOrdersThisMonth() {
        return orderedMapper.countDailyOrdersThisMonth();
    }

    /**
     * 订单发货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(Integer oid, String logisticsCompany, String trackingNumber, String note) throws Exception {
        // 1. 校验订单是否存在
        Ordered order = orderedMapper.getOrderById(oid);
        if (order == null) {
            throw new Exception("订单不存在");
        }

        // 打印日志，确认到底读到了什么
        System.out.println("DEBUG: Order ID=" + oid + ", Status=" + order.getStatus());

        // 只要状态不是 3(已完成) 和 4(已取消)，都允许操作
        if (order.getStatus() == 3 || order.getStatus() == 4) {
            throw new RuntimeException("当前订单状态不可操作发货，状态码：" + order.getStatus());
        }

        // 2. 插入或更新物流信息到 order_logistics 表
        OrderLogistics logistics = orderLogisticsMapper.selectByOrderId(oid);
        boolean isUpdate = (logistics != null);

        if (logistics == null) {
            logistics = new OrderLogistics();
            logistics.setOrderId(oid);
            logistics.setCreatedTime(LocalDateTime.now());
        }

        logistics.setLogisticsCompany(logisticsCompany);
        logistics.setTrackingNumber(trackingNumber);
        logistics.setOrderStatus(2); // 2=已发货
        logistics.setRemarks(note);
        logistics.setUpdatedTime(LocalDateTime.now());

        int rows;
        if (isUpdate) {
            rows = orderLogisticsMapper.updateByOrderId(logistics);
        } else {
            rows = orderLogisticsMapper.insert(logistics);
        }

        if (rows <= 0) {
            throw new Exception("物流信息保存失败");
        }

        // 3. 更新订单状态为已发货
        int result = orderedMapper.updateOrderStatus(oid, 2);
        return result > 0;
    }
}