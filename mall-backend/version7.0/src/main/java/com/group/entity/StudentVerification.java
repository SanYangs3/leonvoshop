package com.group.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentVerification {
    private Integer id;
    private Integer uid;
    private String school;
    private String studentId;
    private String cardImage;
    private Integer status; // 0: 待审核, 1: 已通过, 2: 已拒绝
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}