package com.group.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Feedback {
    private Integer fid;         // 评价ID
    private Integer uid;         // 用户ID
    private Integer pid;         // 商品ID
    private Date feedTime;       // 评价时间
    private Integer star;        // 星级
    private String comment;      // 评价内容

    // 可选：关联信息
    private User user;           // 评价用户
    private Product product;     // 评价商品
}