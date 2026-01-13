package com.group.entity.DTO;

import lombok.Data;

@Data
public class BindEmailRequest {
    private Long uid;
    private String email;
    private String code;
}

