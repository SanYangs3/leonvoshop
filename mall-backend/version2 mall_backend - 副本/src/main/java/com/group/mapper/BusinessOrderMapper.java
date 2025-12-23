// 创建新文件：BusinessOrderMapper.java
package com.group.mapper;

import com.group.entity.Ordered;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BusinessOrderMapper {

    /*
    获取商家的所有订单（不显示status=3的订单）
    逻辑：通过orderItem表中的bid找到相关订单
     */
    @Select("SELECT DISTINCT o.* " +
            "FROM ordered o " +
            "INNER JOIN orderItem oi ON o.oid = oi.oid " +
            "WHERE oi.bid = #{bid} AND o.status != 3 " +
            "ORDER BY o.order_time DESC")
    List<Ordered> getOrdersByBusinessId(@Param("bid") Integer bid);

    /*
    获取商家待发货订单（status=0）
     */
    @Select("SELECT DISTINCT o.* " +
            "FROM ordered o " +
            "INNER JOIN orderItem oi ON o.oid = oi.oid " +
            "WHERE oi.bid = #{bid} AND o.status = 0 " +
            "ORDER BY o.order_time DESC")
    List<Ordered> getPendingOrdersByBusinessId(@Param("bid") Integer bid);

    /*
    验证订单是否属于该商家
     */
    @Select("SELECT COUNT(*) > 0 " +
            "FROM orderItem " +
            "WHERE oid = #{oid} AND bid = #{bid}")
    boolean isOrderBelongsToBusiness(@Param("oid") Integer oid, @Param("bid") Integer bid);

    /*
    获取订单状态
     */
    @Select("SELECT status FROM ordered WHERE oid = #{oid}")
    Integer getOrderStatus(@Param("oid") Integer oid);

    /*
    更新订单状态为已发货（status=1）
     */
    @Update("UPDATE ordered SET status = 1 WHERE oid = #{oid} AND status = 0")
    int updateOrderToShipped(@Param("oid") Integer oid);
}