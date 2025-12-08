package com.lenovo.mall_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;


    private String username;


    private String password;


    private String email;

    private String phone;


    private String avatar;


    private Integer status = 1; // 1正常, 0禁用


    private String role = "USER";


    private LocalDateTime createTime;


    private LocalDateTime updateTime;
}
