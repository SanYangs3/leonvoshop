package com.group.mapper;

import com.group.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

    /*
    根据用户id获取用户订单
     */
    @Select("select * from ordered where uid = #{uid}")
    Order getOrderByUserId(Long uid);
}
