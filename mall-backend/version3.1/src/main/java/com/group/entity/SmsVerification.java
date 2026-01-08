package com.group.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsVerification {
    private Integer id;
    private String phone;
    private String code;
    private LocalDateTime sendTime;
    private LocalDateTime expireTime;
    private Boolean used;
    private LocalDateTime verifyTime;
    private String purpose;
    private String ipAddress;
    private LocalDateTime createdAt;
}
