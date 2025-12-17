// file: SmsService.java
package com.lenovo.mall_backend.service;

import com.lenovo.mall_backend.entity.SmsCode;
import com.lenovo.mall_backend.repository.SmsCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SmsService {

    @Autowired
    private SmsCodeRepository smsCodeRepository;

    private static final String SMS_API_URL = "https://push.spug.cc/sms/yKmaCuBTT86nBx_t0rtWcg";
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRE_MINUTES = 5; // 验证码有效期5分钟
    private static final int RESEND_INTERVAL_SECONDS = 60; // 重新发送间隔60秒

    /**
     * 生成随机验证码
     */
    public String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送短信验证码
     */
    public boolean sendSmsCode(String phone) {
        try {
            // 检查是否在发送间隔内
            SmsCode lastCode = smsCodeRepository.findTopByPhoneOrderBySendTimeDesc(phone);
            if (lastCode != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime lastSendTime = lastCode.getSendTime();
                if (lastSendTime.plusSeconds(RESEND_INTERVAL_SECONDS).isAfter(now)) {
                    // 还在冷却时间内
                    return false;
                }
            }

            // 生成验证码
            String code = generateCode();

            // 调用短信API
            String json = String.format("{\"code\":\"%s\",\"to\":\"%s\"}", code, phone);

            HttpURLConnection conn = (HttpURLConnection) new URL(SMS_API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            // 读取响应
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            // 保存验证码到数据库
            SmsCode smsCode = new SmsCode();
            smsCode.setPhone(phone);
            smsCode.setCode(code);
            smsCode.setSendTime(LocalDateTime.now());
            smsCode.setExpireTime(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
            smsCode.setUsed(false);

            smsCodeRepository.save(smsCode);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证短信验证码
     */
    public boolean verifyCode(String phone, String code) {
        // 查找最新未使用的验证码
        SmsCode smsCode = smsCodeRepository.findByPhoneAndUsedFalseAndExpireTimeAfter(
                phone, LocalDateTime.now());

        if (smsCode == null || !smsCode.getCode().equals(code)) {
            return false;
        }

        // 标记为已使用
        smsCode.setUsed(true);
        smsCode.setVerifyTime(LocalDateTime.now());
        smsCodeRepository.save(smsCode);

        return true;
    }

    /**
     * 检查是否可以重新发送
     */
    public boolean canResend(String phone) {
        SmsCode lastCode = smsCodeRepository.findTopByPhoneOrderBySendTimeDesc(phone);
        if (lastCode == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        return lastCode.getSendTime().plusSeconds(RESEND_INTERVAL_SECONDS).isBefore(now);
    }

    /**
     * 获取剩余时间（秒）
     */
    public long getRemainingSeconds(String phone) {
        SmsCode lastCode = smsCodeRepository.findTopByPhoneOrderBySendTimeDesc(phone);
        if (lastCode == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSendTime = lastCode.getSendTime().plusSeconds(RESEND_INTERVAL_SECONDS);

        if (nextSendTime.isBefore(now)) {
            return 0;
        }

        return java.time.Duration.between(now, nextSendTime).getSeconds();
    }
}