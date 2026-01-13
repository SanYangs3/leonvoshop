package com.group.entity.DTO;

import lombok.Data;

@Data
public class SmsPasswordResetRequest {
    private String phone;
    private String code;
    private String newPassword;
    private String confirmPassword;
}

