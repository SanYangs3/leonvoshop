package com.group.mapper;

import com.group.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FeedbackMapper {

    // 15. 获取对应商品对应评价
    @Select("SELECT f.fid, f.pid, f.star, f.feed_time as feedTime, f.comment, " +
            "u.username, u.avatar " +
            "FROM feedback f " +
            "LEFT JOIN user u ON f.uid = u.uid " +
            "WHERE f.pid = #{pid} " +
            "ORDER BY f.feed_time DESC")
    List<Feedback> getFeedbackByProductId(@Param("pid") Integer pid);

    // 获取所有评价数
    @Select("SELECT COUNT(*) FROM feedback")
    Integer countAllFeedbacks();
}