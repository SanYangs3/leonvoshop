package com.group.mapper;

import com.group.entity.Feedback;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FeedbackMapper {

    // 15. 获取对应商品对应评价
    @Select("SELECT f.fid, f.uid, f.pid, f.star, f.feed_time as feedTime, f.comment, " +
            "u.username, u.avatar " +
            "FROM feedback f " +
            "LEFT JOIN user u ON f.uid = u.uid " +
            "WHERE f.pid = #{pid} " +
            "ORDER BY f.feed_time DESC")
    List<Feedback> getFeedbackByProductId(@Param("pid") Integer pid);

    // 获取所有评价数
    @Select("SELECT COUNT(*) FROM feedback")
    Integer countAllFeedbacks();

    // 插入评价
    @Insert("INSERT INTO feedback (uid, pid, star, comment, feed_time) " +
            "VALUES (#{uid}, #{pid}, #{star}, #{comment}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "fid")
    int insertFeedback(Feedback feedback);

    // 检查用户是否已评价过该商品
    @Select("SELECT COUNT(*) FROM feedback WHERE uid = #{uid} AND pid = #{pid}")
    int checkUserFeedbackExists(@Param("uid") Integer uid, @Param("pid") Integer pid);

    // 获取用户对某商品的评价
    @Select("SELECT * FROM feedback WHERE uid = #{uid} AND pid = #{pid}")
    Feedback getFeedbackByUserAndProduct(@Param("uid") Integer uid, @Param("pid") Integer pid);

    // 更新评价
    @Update("UPDATE feedback SET star = #{star}, comment = #{comment}, feed_time = NOW() " +
            "WHERE uid = #{uid} AND pid = #{pid}")
    int updateFeedback(Feedback feedback);
}