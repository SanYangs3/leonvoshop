package com.group.service.email;

import com.group.service.support.VerificationCodeCache;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailVerificationService {

    private static final int CODE_LENGTH = 6;
    private static final int TTL_SECONDS = 60;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final VerificationCodeCache cache;
    private final EmailSender emailSender;

    public EmailVerificationService(VerificationCodeCache cache, EmailSender emailSender) {
        this.cache = cache;
        this.emailSender = emailSender;
    }

    public void sendCode(String email, String purpose) {
        String normalizedPurpose = (purpose == null || purpose.isBlank()) ? "register" : purpose;
        String key = buildKey(email, normalizedPurpose);
        String code = generateCode();
        boolean accepted = cache.saveIfAbsent(key, code, TTL_SECONDS);
        if (!accepted) {
            throw new IllegalStateException("验证码已发送，请稍后再试");
        }
        emailSender.sendVerificationMail(email, code, normalizedPurpose);
    }

    public boolean verify(String email, String purpose, String code) {
        String normalizedPurpose = (purpose == null || purpose.isBlank()) ? "register" : purpose;
        return cache.validate(buildKey(email, normalizedPurpose), code);
    }

    public long remainingSeconds(String email, String purpose) {
        String normalizedPurpose = (purpose == null || purpose.isBlank()) ? "register" : purpose;
        return cache.remainingSeconds(buildKey(email, normalizedPurpose));
    }

    private String buildKey(String email, String purpose) {
        return "email:" + purpose + ":" + email;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}

