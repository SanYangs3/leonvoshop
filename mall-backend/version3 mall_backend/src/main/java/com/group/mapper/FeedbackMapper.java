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

    // 插入新评价
    @Insert("INSERT INTO feedback (uid, pid, star, feed_time, comment) " +
            "VALUES (#{uid}, #{pid}, #{star}, NOW(), #{comment})")
    @Options(useGeneratedKeys = true, keyProperty = "fid")
    void insertFeedback(Feedback feedback);
}