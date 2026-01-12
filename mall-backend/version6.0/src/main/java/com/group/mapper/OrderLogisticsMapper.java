// ==========================================
// 1. Mapper接口: com.group.mapper.OrderLogisticsMapper
// ==========================================
package com.group.mapper;

import com.group.entity.OrderLogistics;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderLogisticsMapper {
    @Insert("INSERT INTO order_logistics (order_id, logistics_company, tracking_number) " +
            "VALUES (#{orderId}, #{logisticsCompany}, #{trackingNumber})")
    int insert(OrderLogistics logistics);

    @Select("SELECT * FROM order_logistics WHERE order_id = #{orderId}")
    OrderLogistics selectByOrderId(Integer orderId);

    @org.apache.ibatis.annotations.Update("UPDATE order_logistics SET logistics_company = #{logisticsCompany}, " +
            "tracking_number = #{trackingNumber}, updated_time = NOW() WHERE order_id = #{orderId}")
    int updateByOrderId(OrderLogistics logistics);
}