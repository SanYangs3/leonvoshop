
package com.group.service.impl;

import com.group.entity.Feedback;
import com.group.mapper.FeedbackMapper;
import com.group.service.FeedbackService;
import com.group.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private ProductService productService;

    @Override
    public List<Feedback> getFeedbackByProductId(Integer pid) {
        return feedbackMapper.getFeedbackByProductId(pid);
    }

    @Override
    @Transactional
    public void addFeedback(Feedback feedback) {
        // 1. 插入评价
        feedbackMapper.insertFeedback(feedback);

        // 2. 更新商品评分
        if (feedback.getStar() != null) {
            productService.updateRatingByFeedback(feedback.getPid(), feedback.getStar());
        }
    }
}