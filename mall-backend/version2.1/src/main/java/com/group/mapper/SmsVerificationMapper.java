package com.group.mapper;

import com.group.entity.SmsVerification;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;


@Mapper
public interface SmsVerificationMapper {
    @Insert("INSERT INTO sms_verification(phone, code, send_time, expire_time, purpose, ip_address) " +
            "VALUES(#{phone}, #{code}, #{sendTime}, #{expireTime}, #{purpose}, #{ipAddress})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSmsVerification(SmsVerification smsVerification);

    @Select("SELECT * FROM sms_verification WHERE phone = #{phone} ORDER BY send_time DESC LIMIT 1")
    SmsVerification selectLatestByPhone(String phone);

    @Select("SELECT * FROM sms_verification WHERE phone = #{phone} AND used = 0 " +
            "AND expire_time > #{now} ORDER BY send_time DESC LIMIT 1")
    SmsVerification selectValidByPhone(@Param("phone") String phone, @Param("now") LocalDateTime now);

    @Update("UPDATE sms_verification SET used = 1, verify_time = #{verifyTime} WHERE id = #{id}")
    int markAsUsed(@Param("id") Integer id, @Param("verifyTime") LocalDateTime verifyTime);

    @Select("SELECT COUNT(*) FROM sms_verification WHERE phone = #{phone} " +
            "AND DATE(send_time) = CURDATE()")
    long countTodaySentByPhone(String phone);

    @Delete("DELETE FROM sms_verification WHERE expire_time < #{now}")
    int deleteExpiredRecords(LocalDateTime now);
}
