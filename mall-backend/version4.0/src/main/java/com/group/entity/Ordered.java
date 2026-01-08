package com.group.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Ordered {
    private Integer oid;                 // 订单ID
    private Integer status;              // 状态：0待发货,1已发货,2已收货,3取消
    private Date orderTime;             // 订单时间
    private BigDecimal amount;           // 实付款
    private Integer uid;                // 用户ID
    private String province;            // 省份 ← 新增字段
    private String receiverAddress;     // 详细地址
    private String receiverName;        // 联系人
    private String receiverPhone;       // 联系电话
    private String remark;              // 备注

    // 状态文字描述
    public String getStatusText() {
        switch (status) {
            case 0: return "待发货";
            case 1: return "已发货";
            case 2: return "已收货";
            case 3: return "已取消";
            default: return "未知";
        }
    }

    // 关联信息（可选）
    private User user;                  // 用户信息
    private List<OrderItem> items;      // 订单项列表
    
    // 物流信息
    private java.util.Map<String, Object> logistics;

    // 默认构造函数
    public Ordered() {}
}