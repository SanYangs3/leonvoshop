package com.group.entity.DTO;

import lombok.Data;

@Data
public class EmailPasswordResetRequest {
    private String email;
    private String code;
    private String newPassword;
    private String confirmPassword;
}

