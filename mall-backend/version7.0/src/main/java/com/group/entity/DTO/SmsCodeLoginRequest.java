package com.group.entity.DTO;

import lombok.Data;

@Data
public class SmsCodeLoginRequest {
    private String phone;
    private String code;
}

