// 商家登录请求实体类
package com.group.entity;

import lombok.Data;

@Data
public class BusinessLoginRequest {
    private String bname;     // 商家名称
    private String password;  // 密码
}