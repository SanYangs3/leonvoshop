package com.group.mapper;

import com.group.entity.Ordered;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderedMapper {

    @Select("SELECT oid, status, order_time as orderTime, amount, uid, " +
            "province, receiver_address as receiverAddress, " +
            "receiver_name as receiverName, receiver_phone as receiverPhone, remark " +
            "FROM ordered WHERE uid = #{uid} ORDER BY order_time DESC")
    List<Ordered> getOrdersByUserId(@Param("uid") Long uid);

    @Select("SELECT COUNT(*) FROM ordered")
    Integer countAllOrders();

    @Select("SELECT COUNT(*) FROM ordered WHERE DATE(order_time) = CURDATE()")
    Integer countTodayOrders();

    // 新增：获取昨天订单数
    @Select("SELECT COUNT(*) FROM ordered WHERE DATE(order_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)")
    Integer countYesterdayOrders();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM ordered")
    BigDecimal getTotalSales();

    @Select("SELECT oid, status, order_time as orderTime, amount, uid, " +
            "province, receiver_address as receiverAddress, " +
            "receiver_name as receiverName, receiver_phone as receiverPhone, remark " +
            "FROM ordered ORDER BY order_time DESC")
    List<Ordered> getAllOrders();

    @Select("SELECT oid, status, order_time as orderTime, amount, uid, " +
            "province, receiver_address as receiverAddress, " +
            "receiver_name as receiverName, receiver_phone as receiverPhone, remark " +
            "FROM ordered WHERE oid = #{oid}")
    Ordered getOrderById(@Param("oid") Integer oid);

    // 新增：按省份统计订单数量
    @Select("SELECT province, COUNT(*) as count FROM ordered GROUP BY province ORDER BY count DESC")
    List<Map<String, Object>> countOrdersByProvince();

    // 新增：按省份统计销售额
    @Select("SELECT province, SUM(amount) as total_sales FROM ordered GROUP BY province ORDER BY total_sales DESC")
    List<Map<String, Object>> getSalesByProvince();

    // 新增：查询某个省份的订单
    @Select("SELECT oid, status, order_time as orderTime, amount, uid, " +
            "province, receiver_address as receiverAddress, " +
            "receiver_name as receiverName, receiver_phone as receiverPhone, remark " +
            "FROM ordered WHERE province = #{province} ORDER BY order_time DESC")
    List<Ordered> getOrdersByProvince(@Param("province") String province);

    // 11. 获取今日各省份订单数
    @Select("SELECT province, COUNT(*) as today_order_count " +
            "FROM ordered " +
            "WHERE DATE(order_time) = CURDATE() " +
            "GROUP BY province " +
            "ORDER BY today_order_count DESC")
    List<Map<String, Object>> countTodayOrdersByProvince();

    // 获取今日各省份销售额
    @Select("SELECT province, SUM(amount) as today_sales " +
            "FROM ordered " +
            "WHERE DATE(order_time) = CURDATE() " +
            "GROUP BY province " +
            "ORDER BY today_sales DESC")
    List<Map<String, Object>> getTodaySalesByProvince();

    // 新增：获取今天销售额
    @Select("SELECT COALESCE(SUM(amount), 0) FROM ordered WHERE DATE(order_time) = CURDATE()")
    BigDecimal getTodaySales();

    // 新增：获取昨天销售额
    @Select("SELECT COALESCE(SUM(amount), 0) FROM ordered WHERE DATE(order_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)")
    BigDecimal getYesterdaySales();

    /*
    新增订单
    */
    @Insert("INSERT INTO ordered (status, order_time, amount, uid, province, " +
            "receiver_address, receiver_name, receiver_phone, remark) " +
            "VALUES (#{status}, #{orderTime}, #{amount}, #{uid}, #{province}, " +
            "#{receiverAddress}, #{receiverName}, #{receiverPhone}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "oid")
    int insertOrder(Ordered order);

    /*
    更新订单状态
     */
    @Update("UPDATE ordered SET status = #{status} WHERE oid = #{oid}")
    int updateOrderStatus(@Param("oid") Integer oid, @Param("status") Integer status);

    /*
    根据订单ID和用户ID查询订单（用于验证订单归属）
     */
    @Select("SELECT * FROM ordered WHERE oid = #{oid} AND uid = #{uid}")
    Ordered getOrderByOidAndUid(@Param("oid") Integer oid, @Param("uid") Integer uid);

    /*
    根据订单ID查询订单状态
     */
    @Select("SELECT status FROM ordered WHERE oid = #{oid}")
    Integer getOrderStatusById(Integer oid);
}