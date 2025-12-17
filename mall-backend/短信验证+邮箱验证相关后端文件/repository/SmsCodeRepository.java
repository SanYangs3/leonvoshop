// file: SmsCodeRepository.java
package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.SmsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SmsCodeRepository extends JpaRepository<SmsCode, Integer> {

    // 查找手机号最新的验证码记录
    SmsCode findTopByPhoneOrderBySendTimeDesc(String phone);

    // 查找指定手机号未使用且未过期的验证码
    @Query("SELECT s FROM SmsCode s WHERE s.phone = :phone AND s.used = false AND s.expireTime > :now ORDER BY s.sendTime DESC")
    SmsCode findByPhoneAndUsedFalseAndExpireTimeAfter(
            @Param("phone") String phone,
            @Param("now") LocalDateTime now);
}