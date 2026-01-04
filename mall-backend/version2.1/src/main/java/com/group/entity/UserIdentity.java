package com.group.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
用户身份实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity {
    // 身份ID
    private Integer iid;

    // 等级：0~5
    private Integer level;

    // 是否学生：1学生，0普通
    private Integer student;

    // 积分：1元1分，5000分一段
    private Integer points;

    // 用户ID
    private Integer uid;
}
