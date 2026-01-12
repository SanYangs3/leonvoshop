package com.group.service.support;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单内存缓存验证码，避免持久化，满足 60s 有效期需求。
 */
@Component
public class VerificationCodeCache {

    private static final class CachedCode {
        private final String code;
        private final LocalDateTime expireAt;

        private CachedCode(String code, LocalDateTime expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    private final Map<String, CachedCode> cache = new ConcurrentHashMap<>();

    public boolean saveIfAbsent(String key, String code, int ttlSeconds) {
        CachedCode existing = cache.get(key);
        if (existing != null && !isExpired(existing)) {
            return false;
        }
        cache.put(key, new CachedCode(code, LocalDateTime.now().plusSeconds(ttlSeconds)));
        return true;
    }

    public boolean validate(String key, String code) {
        CachedCode cached = cache.get(key);
        if (cached == null) {
            return false;
        }
        if (isExpired(cached)) {
            cache.remove(key);
            return false;
        }
        boolean match = cached.code.equals(code);
        if (match) {
            cache.remove(key);
        }
        return match;
    }

    public long remainingSeconds(String key) {
        CachedCode cached = cache.get(key);
        if (cached == null) {
            return 0;
        }
        if (isExpired(cached)) {
            cache.remove(key);
            return 0;
        }
        return Math.max(0, java.time.Duration.between(LocalDateTime.now(), cached.expireAt).getSeconds());
    }

    private boolean isExpired(CachedCode cached) {
        return cached.expireAt.isBefore(LocalDateTime.now());
    }
}

