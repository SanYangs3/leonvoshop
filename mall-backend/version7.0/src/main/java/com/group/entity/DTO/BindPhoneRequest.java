package com.group.entity.DTO;

import lombok.Data;

@Data
public class BindPhoneRequest {
    private Long uid;
    private String phone;
    private String code;
}

