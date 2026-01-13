package com.group.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class SalesComparisonVO {
    private BigDecimal todaySales;      // 今日销售额
    private BigDecimal yesterdaySales;  // 昨日销售额
    private BigDecimal changePercent;   // 变化百分比

    public SalesComparisonVO(BigDecimal todaySales, BigDecimal yesterdaySales) {
        this.todaySales = todaySales != null ? todaySales : BigDecimal.ZERO;
        this.yesterdaySales = yesterdaySales != null ? yesterdaySales : BigDecimal.ZERO;

        if (yesterdaySales != null && yesterdaySales.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal diff = this.todaySales.subtract(this.yesterdaySales);
            this.changePercent = diff.multiply(BigDecimal.valueOf(100))
                    .divide(this.yesterdaySales, 2, RoundingMode.HALF_UP);
        } else if (this.todaySales.compareTo(BigDecimal.ZERO) > 0) {
            this.changePercent = BigDecimal.valueOf(100); // 从0增长到正数
        } else {
            this.changePercent = BigDecimal.ZERO;
        }
    }
}