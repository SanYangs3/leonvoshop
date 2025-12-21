package com.group.mapper;

import com.group.entity.Business;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BusinessMapper {

    // 14. 获取所有商家信息
    @Select("SELECT bid, bname FROM business ORDER BY bid")
    List<Business> getAllBusinesses();

    // 统计商家数量
    @Select("SELECT COUNT(*) FROM business")
    Integer countAllBusinesses();
}