package com.lenovo.mall_backend.entity;



import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 * 对应数据库表：order（注意：order是SQL关键字，所以需要特殊处理）
 */
@Entity
@Table(name = "`order`")  // 使用反引号避免SQL关键字冲突
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;  // 主键ID

    @Column(name = "status", length = 2)
    private String status = "0";  // 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款

    @Column(name = "order_time", length = 30)
    private String orderTime;  // 订单时间

    @Column(name = "gwc", length = 100)
    private String gwc;  // 购物车商品JSON

    @Column(name = "amount", length = 100)
    private String amount;  // 订单总金额

    @Column(name = "user_id")
    private Long userId;  // 用户ID

    @Column(name = "order_number", length = 13)
    private String orderNumber;  // 订单编号

    @Column(name = "receiver_address", length = 50)
    private String receiverAddress;  // 收货地址

    @Column(name = "receiver_name", length = 20)
    private String receiverName;  // 收货人姓名

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;  // 收货人电话

    @Column(name = "remark", length = 30)
    private String remark;  // 订单备注

    // 以下为业务字段，不在数据库中存储
    @Transient
    private List<com.lenovo.mall_backend.entity.OrderItem> orderItems;  // 订单项列表

    @Transient
    private com.lenovo.mall_backend.entity.User user;  // 用户信息（关联查询）

    @Transient
    private Double amountDouble;  // 金额的Double类型

    /**
     * 获取订单状态名称
     */
    public String getStatusName() {
        switch (status) {
            case "0": return "待支付";
            case "1": return "已支付";
            case "2": return "已发货";
            case "3": return "已完成";
            case "4": return "已取消";
            case "5": return "退款中";
            case "6": return "已退款";
            default: return "未知状态";
        }
    }

    /**
     * 获取Double类型的金额
     */
    public Double getAmountDouble() {
        if (amountDouble == null && amount != null && !amount.isEmpty()) {
            try {
                amountDouble = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                amountDouble = 0.0;
            }
        }
        return amountDouble;
    }

    /**
     * 设置金额（同时设置字符串和Double类型）
     */
    public void setAmountDouble(Double amount) {
        this.amountDouble = amount;
        this.amount = amount != null ? String.format("%.2f", amount) : "0.00";
    }

    /**
     * 自动生成订单编号（如果未设置）
     */
    @PrePersist
    protected void onCreate() {
        // 设置订单时间
        if (orderTime == null) {
            orderTime = LocalDateTime.now().toString();
        }

        // 生成订单编号
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }

        // 设置默认状态
        if (status == null) {
            status = "0";  // 待支付
        }
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNumber() {
        // 格式：ORD + 年月日 + 4位随机数
        String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = (int) (Math.random() * 9000) + 1000;
        return "ORD" + dateStr + random;
    }

    /**
     * 计算订单总金额
     */
    public Double calculateTotalAmount() {
        if (orderItems != null && !orderItems.isEmpty()) {
            double total = orderItems.stream()
                    .mapToDouble(item -> {
                        if (item.getPrice() != null && item.getQuantity() != null) {
                            return item.getPrice() * item.getQuantity();
                        }
                        return 0.0;
                    })
                    .sum();
            setAmountDouble(total);
            return total;
        }
        return 0.0;
    }

    /**
     * 获取订单商品总数
     */
    public Integer getTotalQuantity() {
        if (orderItems != null && !orderItems.isEmpty()) {
            return orderItems.stream()
                    .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                    .sum();
        }
        return 0;
    }

    // 构造方法
    public Order() {
        this.orderItems = new ArrayList<>();
    }

    // 带参数的构造方法
    public Order(Long userId, List<com.lenovo.mall_backend.entity.OrderItem> orderItems, String receiverName,
                 String receiverPhone, String receiverAddress) {
        this();
        this.userId = userId;
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverAddress = receiverAddress;
        this.orderTime = LocalDateTime.now().toString();
        this.status = "0";
        this.orderNumber = generateOrderNumber();
        calculateTotalAmount();
    }

    /**
     * 添加订单项
     */
    public void addOrderItem(com.lenovo.mall_backend.entity.OrderItem item) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(item);
    }

    /**
     * 移除订单项
     */
    public void removeOrderItem(com.lenovo.mall_backend.entity.OrderItem item) {
        if (this.orderItems != null) {
            this.orderItems.remove(item);
        }
    }

    // 确保这个方法参数类型正确
    public void setOrderItems( List<com.lenovo.mall_backend.entity.OrderItem> orderItems) {
        if (orderItems != null) {
            this.orderItems = orderItems;
        } else {
            this.orderItems = new ArrayList<>();
        }
    }


}