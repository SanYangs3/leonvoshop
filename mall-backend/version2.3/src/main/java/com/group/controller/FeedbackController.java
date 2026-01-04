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

    /*
    用户提交评价
     */
    @PostMapping("/submit")
    public Result<Boolean> submitFeedback(@RequestBody FeedbackRequest request) {
        try {
            boolean success = feedbackService.submitFeedback(
                    request.getUid(),
                    request.getPid(),
                    request.getStar(),
                    request.getComment()
            );

            if (success) {
                return Result.success(true);
            } else {
                return Result.error("评价提交失败");
            }
        } catch (Exception e) {
            return Result.error("评价提交失败: " + e.getMessage());
        }
    }

    /*
    用户更新评价
     */
    @PutMapping("/update")
    public Result<Boolean> updateFeedback(@RequestBody FeedbackRequest request) {
        try {
            boolean success = feedbackService.updateFeedback(
                    request.getUid(),
                    request.getPid(),
                    request.getStar(),
                    request.getComment()
            );

            if (success) {
                return Result.success(true);
            } else {
                return Result.error("评价更新失败");
            }
        } catch (Exception e) {
            return Result.error("评价更新失败: " + e.getMessage());
        }
    }

    /*
    检查用户是否已购买该商品
     */
    @GetMapping("/check-purchase/{uid}/{pid}")
    public Result<Boolean> checkUserPurchasedProduct(@PathVariable Integer uid, @PathVariable Integer pid) {
        try {
            boolean purchased = feedbackService.checkUserPurchasedProduct(uid, pid);
            return Result.success(purchased);
        } catch (Exception e) {
            return Result.error("检查购买记录失败: " + e.getMessage());
        }
    }

    /*
    检查用户是否已评价该商品
     */
    @GetMapping("/check-feedback/{uid}/{pid}")
    public Result<Boolean> checkUserFeedbackExists(@PathVariable Integer uid, @PathVariable Integer pid) {
        try {
            boolean exists = feedbackService.checkUserFeedbackExists(uid, pid);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error("检查评价记录失败: " + e.getMessage());
        }
    }

    /*
    获取用户对某商品的评价
     */
    @GetMapping("/user/{uid}/product/{pid}")
    public Result<Feedback> getUserFeedback(@PathVariable Integer uid, @PathVariable Integer pid) {
        try {
            Feedback feedback = feedbackService.getUserFeedback(uid, pid);
            if (feedback == null) {
                return Result.error("用户尚未评价此商品");
            }
            return Result.success(feedback);
        } catch (Exception e) {
            return Result.error("获取用户评价失败: " + e.getMessage());
        }
    }

    // 请求参数类
    static class FeedbackRequest {
        private Integer uid;
        private Integer pid;
        private Integer star;
        private String comment;

        // getters and setters
        public Integer getUid() { return uid; }
        public void setUid(Integer uid) { this.uid = uid; }

        public Integer getPid() { return pid; }
        public void setPid(Integer pid) { this.pid = pid; }

        public Integer getStar() { return star; }
        public void setStar(Integer star) { this.star = star; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

}