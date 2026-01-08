package com.group.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DailyUserGrowthDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyUserGrowthDTO {
    private String date;          // 日期（格式：YYYY-MM-DD）
    private Long userCount;       // 当日用户数
    private Long cumulativeCount; // 累计用户数（可选）
}