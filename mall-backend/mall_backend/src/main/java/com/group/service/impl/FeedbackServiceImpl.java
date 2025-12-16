package com.group.service.impl;

import com.group.entity.Feedback;
import com.group.mapper.FeedbackMapper;
import com.group.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public List<Feedback> getFeedbackByProductId(Integer pid) {
        return feedbackMapper.getFeedbackByProductId(pid);
    }
}