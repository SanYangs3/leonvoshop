package com.group.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderLogistics implements Serializable {
    private Integer id;
    private Integer orderId;
    private String logisticsCompany;
    private String trackingNumber;
    private Integer orderStatus; // 1=未发货，2=已发货，3=已完成
    private String remarks;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}