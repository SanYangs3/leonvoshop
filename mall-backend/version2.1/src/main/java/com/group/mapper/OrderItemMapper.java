package com.group.mapper;

import com.group.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderItemMapper {

    // 根据订单ID获取订单项
    @Select("SELECT oi.*, p.picture as product_picture " +
            "FROM orderItem oi " +
            "LEFT JOIN product p ON oi.pid = p.pid " +
            "WHERE oi.oid = #{oid}")
    List<OrderItem> getItemsByOrderId(@Param("oid") Integer oid);

    // 获取订单的商家信息
    @Select("SELECT DISTINCT oi.bid, b.bname " +
            "FROM orderItem oi " +
            "LEFT JOIN business b ON oi.bid = b.bid " +
            "WHERE oi.oid = #{oid}")
    List<Object> getBusinessesByOrderId(@Param("oid") Integer oid);
}