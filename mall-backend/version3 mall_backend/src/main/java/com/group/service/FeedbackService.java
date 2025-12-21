
package com.group.service;

import com.group.entity.Feedback;
import java.util.List;

public interface FeedbackService {
    /*
    根据商品id获取对应评价
     */
    List<Feedback> getFeedbackByProductId(Integer pid);

    /*
    新增：添加评价（会更新商品评分）
     */
    void addFeedback(Feedback feedback);
}