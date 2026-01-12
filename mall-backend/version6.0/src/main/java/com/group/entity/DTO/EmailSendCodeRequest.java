package com.group.entity.DTO;

import lombok.Data;

@Data
public class EmailSendCodeRequest {
    private String email;
    /**
     * 用途：register 或 reset-password，默认 register
     */
    private String purpose;
}

