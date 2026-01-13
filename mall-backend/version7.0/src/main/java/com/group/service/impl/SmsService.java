package com.group.service.impl;

import com.group.entity.SmsVerification;
import com.group.mapper.SmsVerificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SmsService {

    @Autowired
    private SmsVerificationMapper smsVerificationMapper;

    @Value("${sms.api.url}")
    private String smsApiUrl;

    @Value("${sms.template.name:推送助手A}")
    private String smsTemplateName;

    @Value("${sms.config.code-length:6}")
    private int codeLength;

    @Value("${sms.config.expire-minutes:5}")
    private int expireMinutes;

    @Value("${sms.config.resend-interval:60}")
    private int resendIntervalSeconds;

    @Value("${sms.config.max-daily-count:10}")
    private int maxDailySendCount;

    /**
     * 生成随机验证码
     */
    public String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送短信验证码（根据图片中的调用方式修复）
     */
    @Transactional
    public boolean sendSmsCode(String phone, String purpose, String ipAddress) {
        try {
            LocalDateTime now = LocalDateTime.now();

            // 1. 检查每日发送次数限制
            long todayCount = smsVerificationMapper.countTodaySentByPhone(phone);
            if (todayCount >= maxDailySendCount) {
                throw new RuntimeException("今日发送次数已达上限");
            }

            // 2. 检查是否在发送间隔内
            SmsVerification lastCode = smsVerificationMapper.selectLatestByPhone(phone);
            if (lastCode != null) {
                LocalDateTime lastSendTime = lastCode.getSendTime();
                if (lastSendTime.plusSeconds(resendIntervalSeconds).isAfter(now)) {
                    throw new RuntimeException("发送过于频繁，请稍后再试");
                }
            }

            // 3. 生成验证码
            String code = generateCode();

            // 4. 调用短信API（根据图片中的调用方式）
            boolean sendSuccess = callSmsApi(phone, code);
            if (!sendSuccess) {
                throw new RuntimeException("短信发送失败");
            }

            // 5. 保存验证码记录到数据库（根据我们的数据库表结构）
            SmsVerification smsVerification = new SmsVerification();
            smsVerification.setPhone(phone);
            smsVerification.setCode(code);
            smsVerification.setSendTime(now);
            smsVerification.setExpireTime(now.plusMinutes(expireMinutes));
            smsVerification.setPurpose(purpose != null ? purpose : "register");
            smsVerification.setIpAddress(ipAddress);
            smsVerification.setUsed(false);
            smsVerification.setCreatedAt(now);

            int result = smsVerificationMapper.insertSmsVerification(smsVerification);
            return result > 0;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("发送短信验证码失败: " + e.getMessage());
        }
    }

    /**
     * 调用短信助手API（根据图片中的调用方式修复）
     *
     * 根据图片中的调用方式：
     * URL: https://push.spug.cc/sms/[密钥]
     * 参数格式（可能是GET参数或表单参数，图片显示的是URL参数）：
     * [name]=推送助手A [code]=153146A [number]=5 [to]=186xxxx6688
     *
     * 注意：图片中显示的是URL参数形式，我们需要使用GET请求
     */

    private static final String SMS_API_URL = "https://push.spug.cc/sms/vNvb7DOfSV2wj-qqNxv6YQ";
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRE_MINUTES = 5; // 验证码有效期5分钟
    private static final int RESEND_INTERVAL_SECONDS = 60; // 重新发送间隔60秒

    private boolean callSmsApi(String phone, String code) {
        try {
            // 根据图片信息，短信助手的调用方式有两种可能：
            // 1. URL参数形式：https://push.spug.cc/sms/[密钥]?name=xxx&code=xxx&to=xxx
            // 2. JSON形式（之前的方式）

            // 由于图片显示的是URL参数，我们尝试使用GET请求带参数
           /* String urlWithParams = String.format("%s?name=%s&code=%s&to=%s",
                    smsApiUrl,
                    URLEncoder.encode(smsTemplateName, "UTF-8"),
                    URLEncoder.encode(code, "UTF-8"),
                    URLEncoder.encode(phone, "UTF-8"));

            System.out.println("短信API请求URL: " + urlWithParams);

            // 创建HTTP连接
            HttpURLConnection conn = (HttpURLConnection) new URL(urlWithParams).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);*/

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
            // 获取响应状态码
            int responseCode = conn.getResponseCode();
            System.out.println("短信API响应码: " + responseCode);

            /*// 读取响应内容
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            responseCode == 200 ? conn.getInputStream() : conn.getErrorStream(),
                            StandardCharsets.UTF_8
                    )
            )) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }*/

            String responseBody = response.toString();
            System.out.println("短信API响应: " + responseBody);

            // 判断是否成功（根据短信助手的响应格式）
            return responseCode == 200 && responseBody.contains("成功");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("调用短信API失败: " + e.getMessage());

            // 如果GET方式失败，尝试使用POST JSON方式
            return tryPostJsonApi(phone, code);
        }
    }

    /**
     * 备用方法：使用POST JSON方式调用短信API
     */
    private boolean tryPostJsonApi(String phone, String code) {
        try {
            // 构建JSON请求体（根据图片中的模板变量）
            // 模板内容：$(name)发送，您的验证码为$(code)，$(number)分钟有效。
            String json = String.format(
                    "{\"name\":\"%s\",\"code\":\"%s\",\"number\":%d,\"to\":\"%s\"}",
                    smsTemplateName, code, expireMinutes, phone
            );

            System.out.println("尝试使用POST JSON方式，请求JSON: " + json);

            HttpURLConnection conn = (HttpURLConnection) new URL(smsApiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 发送请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 获取响应状态码
            int responseCode = conn.getResponseCode();
            System.out.println("POST方式响应码: " + responseCode);

            // 读取响应内容
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            responseCode == 200 ? conn.getInputStream() : conn.getErrorStream(),
                            StandardCharsets.UTF_8
                    )
            )) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            String responseBody = response.toString();
            System.out.println("POST方式响应: " + responseBody);

            return responseCode == 200 && responseBody.contains("成功");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("POST JSON方式也失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 验证短信验证码（根据我们的数据库表查询）
     */
    @Transactional
    public boolean verifyCode(String phone, String code) {
        LocalDateTime now = LocalDateTime.now();

        // 查找最新未使用的验证码（根据SmsVerificationMapper的方法）
        SmsVerification smsVerification = smsVerificationMapper.selectValidByPhone(phone, now);

        if (smsVerification == null || !smsVerification.getCode().equals(code)) {
            return false;
        }

        // 标记为已使用
        smsVerificationMapper.markAsUsed(smsVerification.getId(), now);

        return true;
    }

    /**
     * 检查是否可以重新发送
     */
    public boolean canResend(String phone) {
        SmsVerification lastCode = smsVerificationMapper.selectLatestByPhone(phone);
        if (lastCode == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        return lastCode.getSendTime().plusSeconds(resendIntervalSeconds).isBefore(now);
    }

    /**
     * 获取剩余时间（秒）
     */
    public long getRemainingSeconds(String phone) {
        SmsVerification lastCode = smsVerificationMapper.selectLatestByPhone(phone);
        if (lastCode == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSendTime = lastCode.getSendTime().plusSeconds(resendIntervalSeconds);

        if (nextSendTime.isBefore(now)) {
            return 0;
        }

        return java.time.Duration.between(now, nextSendTime).getSeconds();
    }
}