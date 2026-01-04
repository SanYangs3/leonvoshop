package com.group.mapper;

import com.group.entity.Business;
import com.group.entity.BusinessFullInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BusinessMapper {

    // 14. 获取所有商家信息（只包含基本信息）
    @Select("SELECT bid, bname FROM business ORDER BY bid")
    List<Business> getAllBusinesses();

    // 统计商家数量
    @Select("SELECT COUNT(*) FROM business")
    Integer countAllBusinesses();

    // 新增：商家登录验证（返回完整信息）
    @Select("SELECT b.bid, b.bname, b.password, " +
            "bi.status, bi.reg_time as regTime, " +
            "bi.contact_person as contactPerson, " +
            "bi.contact_phone as contactPhone, " +
            "bi.contact_email as contactEmail, " +
            "bi.address, bi.description " +
            "FROM business b " +
            "LEFT JOIN businessInfo bi ON b.bid = bi.bid " +
            "WHERE b.bname = #{bname} AND b.password = #{password}")
    BusinessFullInfo loginWithFullInfo(@Param("bname") String bname, @Param("password") String password);

    // 新增：根据商家ID获取完整商家信息
    @Select("SELECT b.bid, b.bname, b.password, " +
            "bi.status, bi.reg_time as regTime, " +
            "bi.contact_person as contactPerson, " +
            "bi.contact_phone as contactPhone, " +
            "bi.contact_email as contactEmail, " +
            "bi.address, bi.description " +
            "FROM business b " +
            "LEFT JOIN businessInfo bi ON b.bid = bi.bid " +
            "WHERE b.bid = #{bid}")
    BusinessFullInfo getBusinessFullInfoById(@Param("bid") Integer bid);

    // 保留原来的简单登录方法（兼容性）
    @Select("SELECT bid, bname FROM business WHERE bname = #{bname} AND password = #{password}")
    Business login(@Param("bname") String bname, @Param("password") String password);

    // 保留原来的简单查询方法（兼容性）
    @Select("SELECT bid, bname FROM business WHERE bid = #{bid}")
    Business getBusinessById(@Param("bid") Integer bid);

    // 新增：更新商家完整信息
    @Update("<script>" +
            "UPDATE business b " +
            "LEFT JOIN businessInfo bi ON b.bid = bi.bid " +
            "SET " +
            "  b.bname = #{bname}, " +
            "  bi.contact_person = #{contactPerson}, " +
            "  bi.contact_phone = #{contactPhone}, " +
            "  bi.contact_email = #{contactEmail}, " +
            "  bi.address = #{address}, " +
            "  bi.description = #{description} " +
            "WHERE b.bid = #{bid}" +
            "</script>")
    int updateBusinessFullInfo(BusinessFullInfo businessFullInfo);

    // 新增：商家密码修改
    @Update("UPDATE business SET password = #{newPassword} WHERE bid = #{bid} AND password = #{oldPassword}")
    int updatePassword(@Param("bid") Integer bid,
                       @Param("oldPassword") String oldPassword,
                       @Param("newPassword") String newPassword);

    // 新增：检查旧密码是否正确
    @Select("SELECT COUNT(*) FROM business WHERE bid = #{bid} AND password = #{password}")
    int checkPassword(@Param("bid") Integer bid, @Param("password") String password);
}