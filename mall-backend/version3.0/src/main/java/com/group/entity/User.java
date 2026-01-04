package com.group.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/*
  用户实体类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    // 用户ID
    private Integer uid;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 邮箱
    private String email;

    // 电话
    private String phone;

    // 状态：1正常，0禁用
    private Integer status;

    // 头像
    private String avatar;

    // 角色：admin/user
    private String role;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
