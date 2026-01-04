package com.group.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

// UserGrowthTrendDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGrowthTrendDTO {
    private List<DailyUserGrowthDTO> weeklyGrowth;   // 本周每日增长
    private List<DailyUserGrowthDTO> monthlyGrowth;  // 本月每日增长
    private LocalDate weekStartDate;  // 本周开始日期
    private LocalDate weekEndDate;    // 本周结束日期
    private LocalDate monthStartDate; // 本月开始日期
    private LocalDate monthEndDate;   // 本月结束日期
}