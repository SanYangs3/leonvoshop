package com.group.controller;

import com.group.entity.Result;
import com.group.entity.Feedback;
import com.group.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /*
    功能6：发表评价（会自动更新商品评分）
    规则：5星+10，4星+5，3星+0，2星-5，1星-10
     */
    @PostMapping
    public Result<String> addFeedback(@RequestBody Feedback feedback) {
        try {
            // 验证星级
            if (feedback.getStar() == null || feedback.getStar() < 1 || feedback.getStar() > 5) {
                return Result.error("请选择1-5星评价");
            }

            if (feedback.getPid() == null) {
                return Result.error("商品ID不能为空");
            }

            feedbackService.addFeedback(feedback);
            return Result.success("评价发表成功，商品评分已更新");
        } catch (Exception e) {
            return Result.error("评价发表失败：" + e.getMessage());
        }
    }
}