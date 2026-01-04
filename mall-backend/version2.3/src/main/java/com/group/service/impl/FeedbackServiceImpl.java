
package com.group.service.impl;

import com.group.entity.Feedback;
import com.group.mapper.FeedbackMapper;
import com.group.mapper.OrderItemMapper;
import com.group.mapper.ProductMapper;
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
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

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
    /**
     * 用户提交评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitFeedback(Integer uid, Integer pid, Integer star, String comment) throws Exception {
        // 1. 验证用户是否已购买该商品
        if (!checkUserPurchasedProduct(uid, pid)) {
            throw new Exception("您尚未购买此商品，无法评价");
        }

        // 2. 检查用户是否已评价过该商品
        if (checkUserFeedbackExists(uid, pid)) {
            throw new Exception("您已经评价过此商品，如需修改请使用更新评价");
        }

        // 3. 验证评价参数
        if (star == null || star < 1 || star > 5) {
            throw new Exception("评分必须在1-5分之间");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new Exception("评价内容不能为空");
        }

        // 4. 插入评价
        Feedback feedback = new Feedback();
        feedback.setUid(uid);
        feedback.setPid(pid);
        feedback.setStar(star);
        feedback.setComment(comment.trim());

        int result = feedbackMapper.insertFeedback(feedback);
        if (result <= 0) {
            throw new Exception("评价提交失败");
        }

        // 5. 更新商品评分
        updateProductRating(pid);

        return true;
    }

    /**
     * 用户更新评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFeedback(Integer uid, Integer pid, Integer star, String comment) throws Exception {
        // 1. 检查用户是否已评价过该商品
        Feedback existingFeedback = feedbackMapper.getFeedbackByUserAndProduct(uid, pid);
        if (existingFeedback == null) {
            throw new Exception("您尚未评价此商品，请先提交评价");
        }

        // 2. 验证评价参数
        if (star == null || star < 1 || star > 5) {
            throw new Exception("评分必须在1-5分之间");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new Exception("评价内容不能为空");
        }

        // 3. 更新评价
        Feedback feedback = new Feedback();
        feedback.setUid(uid);
        feedback.setPid(pid);
        feedback.setStar(star);
        feedback.setComment(comment.trim());

        int result = feedbackMapper.updateFeedback(feedback);
        if (result <= 0) {
            throw new Exception("评价更新失败");
        }

        // 4. 更新商品评分
        updateProductRating(pid);

        return true;
    }

    /**
     * 检查用户是否已购买该商品
     */
    @Override
    public boolean checkUserPurchasedProduct(Integer uid, Integer pid) throws Exception {
        try {
            int count = orderItemMapper.countPurchasedProduct(uid, pid);
            return count > 0;
        } catch (Exception e) {
            throw new Exception("检查购买记录失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否已评价该商品
     */
    @Override
    public boolean checkUserFeedbackExists(Integer uid, Integer pid) {
        int count = feedbackMapper.checkUserFeedbackExists(uid, pid);
        return count > 0;
    }

    /**
     * 获取用户对某商品的评价
     */
    @Override
    public Feedback getUserFeedback(Integer uid, Integer pid) {
        return feedbackMapper.getFeedbackByUserAndProduct(uid, pid);
    }

    /**
     * 计算并更新商品平均评分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductRating(Integer pid) throws Exception {
        try {
            // 1. 计算平均分
            double avgRating = productMapper.calculateAverageRating(pid);

            // 2. 四舍五入取整
            int roundedRating = (int) Math.round(avgRating);

            // 3. 确保评分在1-5之间
            if (roundedRating < 1) roundedRating = 1;
            if (roundedRating > 5) roundedRating = 5;

            // 4. 更新商品评分
            int result = productMapper.updateRating(pid, roundedRating);
            if (result <= 0) {
                throw new Exception("更新商品评分失败");
            }
        } catch (Exception e) {
            throw new Exception("更新商品评分失败: " + e.getMessage());
        }
    }

}