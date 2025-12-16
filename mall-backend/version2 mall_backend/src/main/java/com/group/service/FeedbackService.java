package com.group.service;

import com.group.entity.Feedback;
import java.util.List;

public interface FeedbackService {
    /*
    根据商品id获取对应评价
     */
    List<Feedback> getFeedbackByProductId(Integer pid);
}