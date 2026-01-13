package com.group.entity.DTO;

import lombok.Data;

@Data
public class EmailCodeLoginRequest {
    private String email;
    private String code;
}

