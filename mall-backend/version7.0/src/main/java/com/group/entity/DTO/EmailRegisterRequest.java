package com.group.entity.DTO;

import lombok.Data;

@Data
public class EmailRegisterRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String username;
    private String phone;
    private String code;
}

