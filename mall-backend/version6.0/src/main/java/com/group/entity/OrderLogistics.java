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
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}