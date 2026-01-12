package com.group.mapper;

import com.group.entity.OrderItem;
import org.apache.ibatis.annotations.*;
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

    /*
   批量插入订单项
    */
    @Insert("<script>" +
            "INSERT INTO orderItem (oid, pid, name, price, quantity, bid) VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.oid}, #{item.pid}, #{item.name}, #{item.price}, " +
            "#{item.quantity}, #{item.bid})" +
            "</foreach>" +
            "</script>")
    int insertOrderItems(@Param("items") List<OrderItem> items);

    /*
    根据订单ID获取订单项（包含商品信息）
     */
    @Select("SELECT oi.*, p.name as product_name, p.picture as product_picture " +
            "FROM orderItem oi " +
            "LEFT JOIN product p ON oi.pid = p.pid " +
            "WHERE oi.oid = #{oid}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "oid", column = "oid"),
            @Result(property = "pid", column = "pid"),
            @Result(property = "name", column = "name"),
            @Result(property = "price", column = "price"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "bid", column = "bid"),
            @Result(property = "subtotal", column = "subtotal"),
            @Result(property = "product.name", column = "product_name"),
            @Result(property = "product.picture", column = "product_picture")
    })
    List<OrderItem> getItemsByOrderIdWithProduct(@Param("oid") Integer oid);

    // 检查用户是否已购买某商品并且已收货
    @Select("SELECT COUNT(*) FROM orderItem oi " +
            "JOIN ordered o ON oi.oid = o.oid " +
            "WHERE o.uid = #{uid} AND oi.pid = #{pid} AND o.status = 2")
    int countPurchasedProduct(@Param("uid") Integer uid, @Param("pid") Integer pid);

    // 获取用户已购买的商品列表（已收货）
    @Select("SELECT DISTINCT oi.pid, p.name " +
            "FROM orderItem oi " +
            "JOIN ordered o ON oi.oid = o.oid " +
            "JOIN product p ON oi.pid = p.pid " +
            "WHERE o.uid = #{uid} AND o.status = 2")
    List<Object> getPurchasedProducts(@Param("uid") Integer uid);
}