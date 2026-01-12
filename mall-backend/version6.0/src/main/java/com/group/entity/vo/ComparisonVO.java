package com.group.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class ComparisonVO {
    private Integer todayValue;      // 今日值
    private Integer yesterdayValue;  // 昨日值
    private BigDecimal changePercent; // 变化百分比（正数为增长，负数为减少）

    public ComparisonVO(Integer todayValue, Integer yesterdayValue) {
        this.todayValue = todayValue;
        this.yesterdayValue = yesterdayValue;

        if (yesterdayValue != 0) {
            this.changePercent = BigDecimal.valueOf((todayValue - yesterdayValue) * 100.0 / yesterdayValue)
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (todayValue > 0) {
            this.changePercent = BigDecimal.valueOf(100); // 从0增长到正数，视为100%增长
        } else {
            this.changePercent = BigDecimal.ZERO; // 都为0
        }
    }
}