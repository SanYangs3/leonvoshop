package com.group.controller;

import com.group.entity.Result;
import com.group.entity.Feedback;
import com.group.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /*
    根据商品id获取对应评价
     */
    @GetMapping("/product/{pid}")
    public Result<List<Feedback>> getFeedbackByProductId(@PathVariable Integer pid) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByProductId(pid);
        return Result.success(feedbacks);
    }
}