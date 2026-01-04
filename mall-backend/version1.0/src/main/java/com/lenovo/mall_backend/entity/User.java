package com.lenovo.mall_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：user
 */
@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;  // 主键ID，使用Long类型对应bigint

    @Column(name = "username", length = 50)
    private String username;  // 用户名

    @Column(name = "password", length = 50)
    private String password;  // 密码

    @Column(name = "role", length = 2)
    private String role;  // 角色：0-普通用户，1-管理员

    @Column(name = "status", length = 1, nullable = false)
    private String status = "1";  // 状态：0-禁用，1-启用

    @Column(name = "nickname", length = 20)
    private String nickname;  // 昵称

    @Column(name = "avatar", length = 100)
    private String avatar;  // 头像URL

    @Column(name = "mobile", length = 13)
    private String mobile;  // 手机号

    @Column(name = "email", length = 50)
    private String email;  // 邮箱

    @Column(name = "gender", length = 1)
    private String gender;  // 性别：M-男，F-女，U-未知

    @Column(name = "description", length = 500)
    private String description;  // 简介

    @Column(name = "create_time", length = 30)
    private String createTime;  // 创建时间

    @Column(name = "score")
    private Integer score = 0;  // 积分，默认0

    @Column(name = "token", length = 32)
    private String token;  // 令牌

    // 以下为业务字段，不在数据库中存储
    @Transient
    private String confirmPassword;  // 确认密码（仅用于注册/修改密码时验证）

    @Transient
    private String newPassword;  // 新密码（用于修改密码）

    // 创建时间自动生成
    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now().toString();
        }
        // 设置默认值
        if (status == null) {
            status = "1";
        }
        if (score == null) {
            score = 0;
        }
    }

    // 获取角色名称
    public String getRoleName() {
        if ("1".equals(role)) {
            return "管理员";
        } else if ("0".equals(role)) {
            return "普通用户";
        } else {
            return "测试用户";
        }
    }

    // 获取状态名称
    public String getStatusName() {
        if ("1".equals(status)) {
            return "启用";
        } else if ("0".equals(status)) {
            return "禁用";
        } else {
            return "未知";
        }
    }

    // 获取性别名称
    public String getGenderName() {
        if ("M".equals(gender)) {
            return "男";
        } else if ("F".equals(gender)) {
            return "女";
        } else if ("U".equals(gender)) {
            return "未知";
        } else {
            return "未知";
        }
    }

    // 构造方法
    public User() {}

    // 带参数的构造方法
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = "1";
        this.score = 0;
    }
}