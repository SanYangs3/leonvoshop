package com.group.service.sms;

import com.group.service.support.VerificationCodeCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Service
public class SmsCodeService {

    private static final int CODE_LENGTH = 6;
    private static final int TTL_SECONDS = 60;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${sms.api.url}")
    private String smsApiUrl;

    @Value("${sms.template.name:推送助手A}")
    private String templateName;

    private final VerificationCodeCache cache;

    public SmsCodeService(VerificationCodeCache cache) {
        this.cache = cache;
    }

    public void sendResetCode(String phone) {
        String key = buildKey(phone);
        String code = generateCode();
        boolean accepted = cache.saveIfAbsent(key, code, TTL_SECONDS);
        if (!accepted) {
            throw new IllegalStateException("验证码已发送，请稍后再试");
        }
        boolean success = callSmsApi(phone, code);
        if (!success) {
            throw new IllegalStateException("短信发送失败");
        }
    }

    public boolean verify(String phone, String code) {
        return cache.validate(buildKey(phone), code);
    }

    public long remainingSeconds(String phone) {
        return cache.remainingSeconds(buildKey(phone));
    }

    private String buildKey(String phone) {
        return "sms:reset:" + phone;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private boolean callSmsApi(String phone, String code) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(smsApiUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 原始实现：仅传 code + to
            String json = String.format("{\"code\":\"%s\",\"to\":\"%s\"}", code, phone);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}

