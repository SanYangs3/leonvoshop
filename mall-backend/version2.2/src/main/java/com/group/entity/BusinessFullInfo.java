package com.group.entity;

import lombok.Data;
import java.util.Date;

@Data
public class BusinessFullInfo {
    // 基本商家信息
    private Integer bid;            // 商家ID
    private String bname;          // 商家名称
    private String password;       // 密码（登录用）

    // 补充商家信息
    private Integer status;         // 状态：1正常，0禁用
    private Date regTime;          // 注册时间
    private String contactPerson;  // 联系人
    private String contactPhone;   // 联系电话
    private String contactEmail;   // 联系邮箱
    private String address;        // 商家地址
    private String description;    // 商家描述

    // 状态文字描述
    public String getStatusText() {
        return status == 1 ? "正常" : "禁用";
    }
}