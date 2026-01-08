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

    /*
    用户提交评价
     */
    boolean submitFeedback(Integer uid, Integer pid, Integer star, String comment) throws Exception;

    /*
    用户更新评价
     */
    boolean updateFeedback(Integer uid, Integer pid, Integer star, String comment) throws Exception;

    /*
    检查用户是否已购买该商品
     */
    boolean checkUserPurchasedProduct(Integer uid, Integer pid) throws Exception;

    /*
    检查用户是否已评价该商品
     */
    boolean checkUserFeedbackExists(Integer uid, Integer pid);

    /*
    获取用户对某商品的评价
     */
    Feedback getUserFeedback(Integer uid, Integer pid);

    /*
    计算并更新商品平均评分
     */
    void updateProductRating(Integer pid) throws Exception;
}