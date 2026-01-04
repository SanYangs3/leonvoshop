package com.group.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class UserCountVO {




        /**
         * 今日用户总数
         */
        private Integer todayCount;

        /**
         * 昨日用户总数
         */
        private Integer yesterdayCount;

        /**
         * 增长率（百分比）
         */
        private BigDecimal growthRate;

        /**
         * 构造方法，自动计算增长率
         */
        public UserCountVO(Integer todayCount, Integer yesterdayCount) {
            this.todayCount = todayCount;
            this.yesterdayCount = yesterdayCount;
            this.growthRate = calculateGrowthRate();
        }

        /**
         * 计算增长率
         */
        private BigDecimal calculateGrowthRate() {
            if (yesterdayCount == null || yesterdayCount == 0) {
                // 如果昨天是0，今天有数据，则视为增长100%
                if (todayCount != null && todayCount > 0) {
                    return new BigDecimal("100.00");
                }
                return BigDecimal.ZERO;
            }

            // 计算增长率：(今天-昨天)/昨天 * 100%
            BigDecimal today = new BigDecimal(todayCount);
            BigDecimal yesterday = new BigDecimal(yesterdayCount);
            BigDecimal difference = today.subtract(yesterday);

            // 保留2位小数
            return difference.divide(yesterday, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }

}
